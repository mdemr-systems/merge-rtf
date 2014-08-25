package com.lostcreekconsulting;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.security.CodeSource;
import java.net.URISyntaxException;
import net.sf.jni4net.Bridge;
import system.windows.forms.RichTextBox;

public class MergeRtf {
    private RichTextBox rtfBox;

    public MergeRtf() {
        rtfBox = new RichTextBox();
    }

    public void addDocument(String rtf) {
        rtfBox.setSelectionStart(rtfBox.getText().length());
        rtfBox.setSelectionLength(0);

        if (rtf.startsWith("{\\rtf"))
            rtfBox.setSelectedRtf(rtf);
        else
            rtfBox.setSelectedText(rtf + "\r\n\r\n");
    }

    public String merged() {
        return rtfBox.getRtf();
    }

    public void dispose() {
        rtfBox.Dispose();
    }

    public static OutputStream merge(Iterable<String> documents) throws IOException, URISyntaxException {
        CodeSource codeSource = MergeRtf.class.getProtectionDomain().getCodeSource();
        File jar = new File(codeSource.getLocation().toURI().getPath());
        String path = jar.getParentFile().getPath();
        File proxies = new File(path, "merge-rtf-proxies.dll");

        Bridge.setVerbose(true);
        Bridge.init();
        Bridge.LoadAndRegisterAssemblyFrom(proxies);

        MergeRtf rtf = new MergeRtf();
        
        for (String document : documents) {
            rtf.addDocument(document);
        }

        OutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        writer.write(rtf.merged());
        writer.flush();
        writer.close();

        rtf.dispose();
        return stream;
    }
}
