package com.vtb.mapping;

import java.util.ArrayList;

import com.vtb.domain.DocumentType;
import com.vtb.custom.DocumentTypeTO;
import com.vtb.domain.DocumentsType;
import com.vtb.exception.MappingException;
/**
 * 
 * изменил @author Какунин Константин Юрьевич
 *
 */
public interface DocumentsTypeMapper extends Mapper<DocumentsType> {
    public ArrayList<DocumentsType> findByName(String name, String orderBy) throws MappingException;

    public ArrayList<DocumentsType> getContractorDocTypes();
    public ArrayList<DocumentsType> getPersonDocTypes();

    public ArrayList<DocumentsType> getOpportunityDocTypes();
    
    public ArrayList<DocumentsType> getDocumentTypesByOwnformType(int Type, String orderBy);
}