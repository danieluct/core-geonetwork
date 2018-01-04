/*
 * Copyright (C) 2001-2016 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */
package org.fao.geonet.api.cssstyle;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.api.API;
import org.fao.geonet.api.cssstyle.service.ICssStyleSettingService;
import org.fao.geonet.domain.CssStyleSetting;
import org.fao.geonet.kernel.GeonetworkDataDirectory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class CssStyleSettingsService.
 */
@RequestMapping(value = { "/api/customstyle", "/api/" + API.VERSION_0_1 + "/customstyle" })
@Api(value = "customstyle", tags = "customstyle", description = "Functionalities for custom styling")
@Controller("stylesheet")
public class CssStyleSettingsService {

    /**
     * Save css style.
     *
     * @param request the request
     * @param response the response
     * @param jsonVariables the json variables
     * @return the response entity
     * @throws Exception the exception
     */
    @ApiOperation(value = "Saves custom style variables.", notes = "Saves custom style variables.", nickname = "saveCustomStyle")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @PreAuthorize("hasRole('Editor')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity saveCssStyle(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response,
            @ApiParam(name = "gnCssStyle") @RequestBody String jsonVariables) throws Exception {

        try {
            final String cleanedVariables = convertFormNamesToLessVariablesNames(jsonVariables);
            saveLessVariablesOnDB(cleanedVariables);
            saveLessVariablesInDataFolder(cleanedVariables);

            return new ResponseEntity(HttpStatus.CREATED);
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets the css style.
     *
     * @param request the request
     * @param response the response
     * @return the css style
     * @throws Exception the exception
     */
    @ApiOperation(value = "Get CssStyleSettings", notes = "This returns a map with all Less variables.", nickname = "getCssStyle")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Map<String, String> getCssStyle(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response)
            throws Exception {

        final ICssStyleSettingService cssStyleSettingService = (ICssStyleSettingService) ApplicationContextHolder.get()
                .getBean("cssStyleSettingService");

        final List<CssStyleSetting> currentCssStyleSettings = cssStyleSettingService.getCustomCssSettings();

        if (currentCssStyleSettings == null) {
            return null;
        } else {
            final Map<String, String> map = convertLessListToAngularFormat(currentCssStyleSettings);
            return map;
        }
    }

    /**
     * Convert less list to map.
     *
     * @param currentCssStyleSettings the current css style settings
     * @return the map
     * @throws JSONException the JSON exception
     */

    private Map<String, String> convertLessListToAngularFormat(List<CssStyleSetting> currentCssStyleSettings) throws JSONException {
        final Map<String, String> map = new HashMap<>();

        for (final CssStyleSetting cssStyleSetting : currentCssStyleSettings) {
            map.put(fromDashToCamel(cssStyleSetting.getVariable()), cssStyleSetting.getValue());
        }
        return map;
    }

    /**
     * Save less variables on DB.
     *
     * @param jsonLessVariables the json less variables
     * @throws JSONException the JSON exception
     */

    private void saveLessVariablesOnDB(String jsonLessVariables) throws JSONException {
        final List<CssStyleSetting> listLessVariables = convertToListOfVariables(jsonLessVariables);
        storeListOnDB(listLessVariables);
    }

    /**
     * Save less variables in data folder.
     *
     * @param cssStyleSettings the css style settings
     * @throws IOException Signals that an I/O exception has occurred.
     */

    private void saveLessVariablesInDataFolder(String cssStyleSettings) throws IOException {
        final String dataFolderPath = getDataFolder();
        final Path lessPath = initializeLessFileInDataFolder(dataFolderPath);

        // Write properties to file
        final Charset charset = StandardCharsets.UTF_8;
        Files.write(lessPath, cssStyleSettings.getBytes(charset));
    }

    /**
     * Gets the data folder.
     *
     * @return the data folder
     */

    private String getDataFolder() {
        final GeonetworkDataDirectory geonetworkDataDirectory = ApplicationContextHolder.get().getBean(GeonetworkDataDirectory.class);
        final String path = geonetworkDataDirectory.getSystemDataDir().resolve("node-less-files").toString();
        return path;
    }

    /**
     * Initialize less file in data folder.
     *
     * @param path the path
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     */

    private Path initializeLessFileInDataFolder(String path) throws IOException {
        final Path lessPath = Paths.get(path + "/gn_dynamic_style.less");
        try {
            Files.createFile(lessPath);
        } catch (final FileAlreadyExistsException e1) {
            Files.delete(lessPath);
            Files.createFile(lessPath);
        } catch (final NoSuchFileException e2) {
            final Path tmp = lessPath.getParent();
            Files.createDirectories(tmp);
            Files.createFile(lessPath);
        }
        return lessPath;
    }

    /**
     * Store list on DB.
     *
     * @param cssVariables the css variables
     */

    private void storeListOnDB(List<CssStyleSetting> cssVariables) {
        final ICssStyleSettingService cssStyleSettingService = (ICssStyleSettingService) ApplicationContextHolder.get()
                .getBean("cssStyleSettingService");
        cssStyleSettingService.saveSettings(cssVariables);
    }

    /**
     * Convert to list of variables.
     *
     * @param jsonLessVariables the json less variables
     * @return the list
     * @throws JSONException the JSON exception
     */

    private List<CssStyleSetting> convertToListOfVariables(String jsonLessVariables) throws JSONException {
        final List<CssStyleSetting> cssVariables = new ArrayList<>();

        final JSONObject jObject = new JSONObject(jsonLessVariables);
        final Iterator<String> iter = jObject.keys();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (jObject.getString(key) != null) {
                cssVariables.add(new CssStyleSetting(key, jObject.getString(key)));
            }
        }
        return cssVariables;
    }

    /**
     * Convert form names to less variables names.
     *
     * @param jsonLessVariables the json less variables
     * @return the string
     * @throws JSONException the JSON exception
     */
    private String convertFormNamesToLessVariablesNames(String jsonLessVariables) throws JSONException {

        final JSONObject input = new JSONObject(jsonLessVariables);
        final JSONObject output = new JSONObject();
        final Iterator<String> iter = input.keys();
        while (iter.hasNext()) {
            final String key = iter.next();
            if (input.getString(key) != null) {
                output.put(fromCamelToDash(key), input.getString(key));
            }
        }
        return output.toString();
    }

    /**
     * Get a string in dashed format and convert to camel format abc-def -> abcDef
     *
     * @param dashFormat the dash format
     * @return the string
     */
    private String fromDashToCamel(String dashFormat) {
        final String upperFormat = LOWER_HYPHEN.to(LOWER_CAMEL, dashFormat);

        return upperFormat;
    }

    /**
     * Get a string in camel format and convert to dashed format abcDef -> abc-def
     *
     * @param upperFormat the upper format
     * @return the string
     */
    private String fromCamelToDash(String upperFormat) {
        final String dashFormat = LOWER_CAMEL.to(LOWER_HYPHEN, upperFormat);

        return dashFormat;
    }

}