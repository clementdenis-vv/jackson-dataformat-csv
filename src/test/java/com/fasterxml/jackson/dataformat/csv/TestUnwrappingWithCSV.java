package com.fasterxml.jackson.dataformat.csv;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.fasterxml.jackson.databind.ObjectMapper;

// for #12
public class TestUnwrappingWithCSV extends ModuleTestBase
{
    @JsonPropertyOrder({"x", "y"})
    final static class Location {
        public int x;
        public int y;

        public Location() { }
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Unwrapping {
        public String name;
        @JsonUnwrapped
        public Location location;

        public Unwrapping() { }
        public Unwrapping(String str, int x, int y) {
            name = str;
            location = new Location(x, y);
        }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    /**
     * Simple test to verify that explicit schema mapping works fine
     * with unwrapped entities
     */
    public void testSimpleUnwrappingRoundtrip() throws Exception
    {
        final String CSV = "Joe,15,27\n";
        ObjectMapper mapper = mapperForCsv();
        CsvSchema schema = CsvSchema.builder()
            .addColumn("name")
            .addColumn("x")
            .addColumn("y")
            .build();
        Unwrapping wrapper = mapper.reader(schema).withType(Unwrapping.class).readValue(CSV);
        assertNotNull(wrapper);
        assertNotNull(wrapper.location);
        assertEquals(15, wrapper.location.x);
        assertEquals(27, wrapper.location.y);

        // should also write out the same way
        assertEquals(CSV, mapper.writer(schema).writeValueAsString(wrapper));
    }

    
    /**
     * Another simple test, but this time auto-generating Schema from
     * POJO.
     */
    /* NOTE: the problem here is that Unwrapped properties should be further
     * detected to find sub-properties -- but that information is not yet
     * available via BeanProperty/POJOPropertyBuilder. But it needs to be
     * made; and when this occurs, we can handle this case reasonably well.
     */
    /*
    public void testSimpleWithAutoSchema() throws Exception
    {
        final String CSV = "Henry,1,2\n";
        CsvMapper mapper = mapperForCsv();
        CsvSchema schema = mapper.schemaFor(Unwrapping.class);
System.err.println("SChema/auto -> "+schema);        
        
        Unwrapping wrapper = mapper.reader(schema).withType(Unwrapping.class).readValue(CSV);
        assertNotNull(wrapper);
        assertNotNull(wrapper.location);
        assertEquals(15, wrapper.location.x);
        assertEquals(27, wrapper.location.y);

        // should also write out the same way
        assertEquals(CSV, mapper.writer(schema).writeValueAsString(wrapper));
    }
    */
}