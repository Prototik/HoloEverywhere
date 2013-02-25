
package org.holoeverywhere.resbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.holoeverywhere.resbuilder.type.strings.TypeStrings;
import org.holoeverywhere.resbuilder.type.styles.TypeStyles;
import org.json.JSONObject;

public class FileProcesser {
    public static class FileProcesserException extends Exception {
        private static final long serialVersionUID = 3229067373448702029L;

        public FileProcesserException(String message) {
            super(message);
        }

        public FileProcesserException(Throwable cause) {
            super(cause);
        }
    }

    public static class ProcessResult extends ArrayList<ProcessResult> {
        private static final class WriterState {
            private File file;
            private StringWriter tempWriter;
            private XMLStreamWriter writer;
        }

        private static final long serialVersionUID = 6761878269956927443L;

        private static final Transformer TRANSFORMER;

        private static final TransformerFactory TRANSFORMER_FACTORY;

        private static final Map<File, WriterState> WRITERS_MAP = new HashMap<File, WriterState>();
        private static final XMLOutputFactory XML_OUTPUT_FACTORY;
        static {
            XML_OUTPUT_FACTORY = XMLOutputFactory.newFactory();
            TRANSFORMER_FACTORY = TransformerFactory.newInstance();
            Transformer transformer = null;
            try {
                transformer = TRANSFORMER_FACTORY.newTransformer();
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            TRANSFORMER = transformer;
        }

        protected void close(BuildMojo mojo) throws FileProcesserException {
            for (ProcessResult child : this) {
                child.close(mojo);
            }
            try {
                synchronized (WRITERS_MAP) {
                    for (WriterState state : WRITERS_MAP.values()) {
                        state.writer.writeEndElement();
                        state.writer.writeEndDocument();
                        state.writer.flush();
                        state.writer.close();
                        TRANSFORMER.transform(
                                new StreamSource(new StringReader(state.tempWriter.toString())),
                                new StreamResult(new FileOutputStream(state.file)));
                    }
                    WRITERS_MAP.clear();
                }
            } catch (Exception e) {
                throw new FileProcesserException(e);
            }
        }

        @SuppressWarnings("unchecked")
        public <T extends ProcessResult> T find(Class<T> clazz) {
            if (clazz.isAssignableFrom(getClass())) {
                return (T) this;
            }
            T result;
            for (ProcessResult child : this) {
                result = child.find(clazz);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        public void flush(BuildMojo mojo) throws FileProcesserException {
            for (ProcessResult child : this) {
                child.flush(mojo);
            }
            close(mojo);
        }

        protected XMLStreamWriter openWriter(BuildMojo mojo, File dir, String filename)
                throws FileProcesserException {
            try {
                if (dir == null) {
                    dir = mojo.outputDir;
                }
                if (!dir.exists()) {
                    if (mojo.verbose) {
                        mojo.getLog().info("Create new folder: " + dir.getAbsolutePath());
                    }
                    dir.mkdirs();
                }
                File file = new File(dir, filename);
                WriterState state;
                synchronized (WRITERS_MAP) {
                    state = WRITERS_MAP.get(file);
                    if (state == null) {
                        if (mojo.verbose) {
                            mojo.getLog().info(" # Flush data to file " + file.getAbsolutePath());
                        }
                        state = new WriterState();
                        state.file = file;
                        state.tempWriter = new StringWriter();
                        state.writer = XML_OUTPUT_FACTORY.createXMLStreamWriter(state.tempWriter);
                        state.writer.writeStartDocument("utf-8", "1.0");
                        state.writer.writeStartElement("resources");
                        WRITERS_MAP.put(file, state);
                    }
                }
                return state.writer;
            } catch (Exception e) {
                throw new FileProcesserException(e);
            }
        }
    }

    private static final Map<String, TypeProcesser> PROCESSERS_MAP;

    static {
        PROCESSERS_MAP = new HashMap<String, TypeProcesser>();
        registerProcesser(TypeStrings.class);
        registerProcesser(TypeStyles.class);
    }

    public static void process(BuildMojo mojo) throws FileProcesserException {
        new FileProcesser(mojo).process();
    }

    public static void registerProcesser(Class<? extends TypeProcesser> clazz) {
        try {
            TypeProcesser processer = clazz.newInstance();
            PROCESSERS_MAP.put(processer.getType(), processer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BuildMojo mojo;

    public FileProcesser(BuildMojo mojo) {
        setMojo(mojo);
    }

    public BuildMojo getMojo() {
        return mojo;
    }

    public void process() throws FileProcesserException {
        ProcessResult result = new ProcessResult();
        for (String input : mojo.inputFiles) {
            result.add(process(input));
        }
        result.flush(mojo);
    }

    public ProcessResult process(File file) throws FileProcesserException {
        return process(file, null);
    }

    @SuppressWarnings("unchecked")
    public ProcessResult process(File file, String forceType) throws FileProcesserException {
        try {
            mojo.getLog().info("Process file: " + file.getAbsolutePath());
            String fileContent = readFile(file);
            JSONObject json = new JSONObject(fileContent);
            if (forceType != null) {
                mojo.getLog().info("Handle all file by key '" + forceType + "' (force)");
                return process(forceType, json);
            }
            if (file.getName().startsWith("key_")) {
                String key = file.getName();
                int c = key.lastIndexOf('.');
                key = key.substring(4, c > 0 ? c : key.length());
                mojo.getLog().info("Handle all file by key '" + key + "'");
                return process(key, json);
            }
            ProcessResult result = new ProcessResult();
            Iterator<String> keys = json.sortedKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                ProcessResult subResult = process(key, json.optJSONObject(key));
                if (subResult != null) {
                    result.add(subResult);
                }
            }
            return result;
        } catch (Exception e) {
            throw new FileProcesserException(e);
        }
    }

    public ProcessResult process(String filename) throws FileProcesserException {
        String forceType = null;
        int c = filename.lastIndexOf(':');
        if (c > 0) {
            // Filename: data.json:styles
            forceType = filename.substring(c + 1);
            filename = filename.substring(0, c);
        }
        File file;
        for (File includeDir : mojo.includeDirs) {
            file = new File(includeDir, filename);
            if (file.exists()) {
                return process(file, forceType);
            }
        }
        if ((file = new File(filename)).exists()) {
            return process(file, forceType);
        }
        throw new FileProcesserException("Couldn't find file for processing: " + filename);
    }

    public ProcessResult process(String key, JSONObject json) throws FileProcesserException {
        TypeProcesser processer = PROCESSERS_MAP.get(key);
        if (processer == null) {
            mojo.getLog().warn(" # Couldn't find processer for key '" + key + "', skip");
            return null;
        }
        mojo.getLog().info(" # Handle key '" + key + "' to processer '" + processer.getName()
                + "'");
        ProcessResult subResult = processer.process(mojo, json);
        if (subResult == null) {
            String message = "Processer " + processer.getClass().getName()
                    + " return null result";
            mojo.getLog().error(" # " + message);
            throw new FileProcesserException(message);
        } else {
            return subResult;
        }
    }

    private String readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        Reader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[1024];
        int c;
        while ((c = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, c);
        }
        reader.close();
        is.close();
        return builder.toString();
    }

    public void setMojo(BuildMojo mojo) {
        this.mojo = mojo;
        this.mojo.processer = this;
    }
}
