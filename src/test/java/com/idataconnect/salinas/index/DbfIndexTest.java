package com.idataconnect.salinas.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.idataconnect.jdbfdriver.DBF;
import com.idataconnect.jdbfdriver.DBFField;
import com.idataconnect.jdbfdriver.index.MDX;
import com.idataconnect.jdbfdriver.index.IndexDataType;

public class DbfIndexTest {

    @Test
    public void testMdxExpressionUpdate() throws Exception {
        File dbfFile = File.createTempFile("test", ".dbf");
        dbfFile.deleteOnExit();
        File mdxFile = new File(dbfFile.getParent(), dbfFile.getName().replace(".dbf", ".mdx"));
        mdxFile.deleteOnExit();

        List<DBFField> fields = new ArrayList<>();
        fields.add(new DBFField("NAME", DBFField.FieldType.C, 20));
        
        DBF dbf = DBF.create(dbfFile, fields);
        MDX mdx = MDX.create(mdxFile, dbfFile.getName());
        
        // Add a tag with a complex expression
        mdx.addTag("UPPERNAME", "UPPER(NAME)", IndexDataType.CHARACTER, false, false);
        dbf.setIndex(mdx);

        // Append a record
        dbf.appendBlank();
        dbf.replace("NAME", "Bob");
        
        // The index should have "BOB" (case-insensitive search in MDX usually handles this, 
        // but here we want to see if UPPER(NAME) was evaluated)
        mdx.setTag("UPPERNAME");
        assertEquals(1, mdx.find("BOB"));
        
        // Replace with new value
        dbf.replace("NAME", "Alice");
        
        // Old key "BOB" should be gone, new key "ALICE" should be there
        assertEquals(DBF.RECORD_NUMBER_EOF, mdx.find("BOB"));
        assertEquals(1, mdx.find("ALICE"));
        
        dbf.close();
    }
}
