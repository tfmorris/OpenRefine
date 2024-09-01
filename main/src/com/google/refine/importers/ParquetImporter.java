/*

Copyright 2024, OpenRefine contributors
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.importers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.io.SeekableInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.ProjectMetadata;
import com.google.refine.importing.ImportingJob;
import com.google.refine.model.Project;
import com.google.refine.util.JSONUtilities;

public class ParquetImporter extends TabularImportingParserBase {
    private static final Logger log = LoggerFactory.getLogger(ParquetImporter.class);

    public ParquetImporter() {
        super(false);
    }

    @Override
    public ObjectNode createParserUIInitializationData(ImportingJob job,
            List<ObjectNode> fileRecords, String format) {
        ObjectNode options = super.createParserUIInitializationData(job, fileRecords, format);

        JSONUtilities.safePut(options, "guessCellValueTypes", false);
        JSONUtilities.safePut(options, "trimStrings", true);

        return options;
    }


    @Override
    public void parseOneFile(
            Project project,
            ProjectMetadata metadata,
            ImportingJob job,
            String fileSource,
            InputStream inputStream,
            int limit,
            ObjectNode options,
            List<Exception> exceptions) {


        //////////


        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        List<Object> retrievedColumnNames = null;
        if (options.has("columnNames")) {
            String[] strings = JSONUtilities.getStringArray(options, "columnNames");
            if (strings.length > 0) {
                retrievedColumnNames = new ArrayList<>();
                for (String s : strings) {
                    s = CharMatcher.whitespace().trimFrom(s);
                    if (!s.isEmpty()) {
                        retrievedColumnNames.add(s);
                    }
                }

                if (!retrievedColumnNames.isEmpty()) {
                    JSONUtilities.safePut(options, "headerLines", 1);
                } else {
                    retrievedColumnNames = null;
                }
            }
        }

        final List<Object> columnNames = retrievedColumnNames;


//        final LineNumberReader lnReader = new LineNumberReader(reader);

        TableDataReader dataReader = new TableDataReader() {

            boolean usedColumnNames = false;

            @Override
            public List<Object> getNextRowOfCells() throws IOException {
                if (columnNames != null && !usedColumnNames) {
                    usedColumnNames = true;
                    return columnNames;
                } else {
                    return getCells();

                }
            }
        };

        TabularImportingParserBase.readTable(project, job, dataReader, limit, options, exceptions);
    }

    static protected List<Object> getCells(/*ParquetReader<GenericRecord> avroReader*/)
            throws IOException {

        List<Object> cells = new ArrayList<>();

        return cells;
    }



    public static void main(String[] args) {

        InputFile inputFile = new LocalInputFile(Paths.get("/Users/tfmorris/Downloads/Titanic.parquet"));

        // This seems to be mandatory because the 1-arg version of genericRecordReader doesn't work
        final ParquetConfiguration plainParquetConf = new PlainParquetConfiguration();

        try (ParquetReader<GenericRecord> avroReader = AvroParquetReader.genericRecordReader(inputFile, plainParquetConf)) {
            Schema schema = null;
            do {
                GenericRecord record = avroReader.read();
                long rowIndex = avroReader.getCurrentRowIndex();
                if (record == null) {
                    break;
                }
                Schema curSchema = record.getSchema();
                if (schema == null) {
                    schema = curSchema;
                } else {
                    if (!schema.equals(curSchema)) {
                        // We don't support changing shape/schema
//                        exceptions.add(new ImportException("Changing schema between records is not supported", null));
                        throw new RuntimeException("Changing schema between records is not supported", null);
                    }
                }
                List<Schema.Field> fields = schema.getFields();
                for (Schema.Field field : fields) {
                    String name = field.name();
                    // TODO: Should we just go by object type and ignore all the rest?
                    Object value = record.get(name);
                    Map<String, Object> props = field.getObjectProps();
                    // TODO: Do we want to store in natural order or using position info? Probably the latter.
                    int pos = field.pos();
                    Schema fSchema = field.schema();
//                    String fName = fSchema.getFullName();
//                    LogicalType lType = fSchema.getLogicalType();
                    if (fSchema.getType() == Schema.Type.UNION) {
                        List<Schema> types = fSchema.getTypes();
                        if (types.size() > 2 || types.get(0).getType() == Schema.Type.NULL) {
                            // A nullable field shows up like this. Anything fancier we probably can't handle
                            throw new RuntimeException("Unsupported union type (only simple nullable unions supported): " + fSchema);
                        }
                    }
                }
            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // TODO: We need something which wraps a generic InputStream and implements a SeakableInputStream - below are just some ideas

//    class StreamInput implements InputFile {
//
//        private final InputStream inputStream;
//
//        StreamInput(InputStream in) {
//            this.inputStream = in;
//        }
//
//        @Override
//        public long getLength() throws IOException {
//            // TODO: What can we get away with implementing here?
//            throw new IOException("getLength not implemented");
////            return Long.MAX_VALUE;
////            return -1;
//        }
//
//        @Override
//        public SeekableInputStream newStream() throws IOException {
//            return new ParquetStream(inputStream);
//        }
//    }
//
//
//
//    public class ParquetStream implements InputFile {
//        private final String streamId;
//        private final byte[] data;
//
//        private static class SeekableByteArrayInputStream extends ByteArrayInputStream {
//            public SeekableByteArrayInputStream(byte[] buf) {
//                super(buf);
//            }
//
//            public void setPos(int pos) {
//                this.pos = pos;
//            }
//
//            public int getPos() {
//                return this.pos;
//            }
//        }
//
//        public ParquetStream(String streamId, ByteArrayOutputStream stream) {
//            this.streamId = streamId;
//            this.data = stream.toByteArray();
//        }
//
//        @Override
//        public long getLength() throws IOException {
//            return this.data.length;
//        }
//
//        @Override
//        public SeekableInputStream newStream() throws IOException {
//            return new DelegatingSeekableInputStream(new SeekableByteArrayInputStream(this.data)) {
//                @Override
//                public void seek(long newPos) throws IOException {
//                    ((SeekableByteArrayInputStream) this.getStream()).setPos((int) newPos);
//                }
//
//                @Override
//                public long getPos() throws IOException {
//                    return ((SeekableByteArrayInputStream) this.getStream()).getPos();
//                }
//            };
//        }
//
//        @Override
//        public String toString() {
//            return "ParquetStream[" + streamId + "]";
//        }
//    }
}
