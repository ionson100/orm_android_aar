package com.bitnic.bitnicorm;
/********************************************************************
 * Copyright © 2016-2017 OOO Bitnic                                 *
 * Created by OOO Bitnic on 08.02.16   corp@bitnic.ru               *
 * ******************************************************************/

import java.util.List;

class CacheMetaDataFree<T> {
    public List<ItemFieldFree> listColumn;

    public CacheMetaDataFree(Class<T> aClass) {
        listColumn = AnnotationOrm.getListItemFieldColumnFree(aClass);
    }
}
