package com.esri.sde.sdk.client;

import com.esri.sde.sdk.client.SeTable.SeTableStats;

public class SeQuery extends SeStreamOp {

    public static short SE_SPATIAL_FIRST = 0;
    public static /* GEOT-947 final*/ short SE_OPTIMIZE = 0;

    public SeQuery(SeConnection c) throws SeException {
    }

    public SeQuery(SeConnection c, String[] s, SeSqlConstruct y) throws SeException {
    }

    public void prepareQuery() throws SeException {
    }

    /**
     * Added by heikki doeleman.
     */
    public void prepareQuery(String[] s, SeSqlConstruct sc) throws SeException {
    }

    public void prepareQueryInfo(SeQueryInfo i) throws SeException {
    }

    public SeExtent calculateLayerExtent(SeQueryInfo i) {
        return null;
    }

    public void cancel(boolean b) {
    }

    public void setRowLocking(int i) {
    }

    public SeRow fetch() throws SeException {
        return null;
    }

    public void setSpatialConstraints(short i, boolean b, SeFilter[] f) throws SeException {
    }

    public SeTableStats calculateTableStatistics(String s, int i, SeQueryInfo q, int j) {
        return null;
    }

    public void queryRasterTile(SeRasterConstraint c) {
    }

    public void prepareSql(String s) {
    }

}
