package es.arnaugris.smtp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SMTPExtractor {

    private final ArrayList<String> urls;
    private final ArrayList<String> hidden;

    public SMTPExtractor() {
        urls = new ArrayList<>();
        hidden = new ArrayList<>();
    }



    public String extractMessage(ArrayList<String> data) {
        String end_boundary = null, boundary = null;
        boolean reading = false;
        StringBuilder message = new StringBuilder();
        StringBuilder completeUrl = null;

        for (String line_string : data) {
            if (boundary != null) {
                if (reading) {
                    if (line_string.contains(end_boundary)) {
                        reading = false;
                        boundary = null;
                    } else {

                        String check = line_string.replaceAll("/r", "").replaceAll("/n", "").replaceAll(" ", "");
                        String substring = "";

                        if (check.length() > 1) {
                            substring = check.trim().substring(check.length() - 1);
                        }

                        if (check.contains("http")  && completeUrl == null && substring.equalsIgnoreCase("=")) {

                            completeUrl = new StringBuilder();

                            String[] split = line_string.split(" ");
                            String word = split[split.length - 1];

                            word = word.replaceAll("/r", "").replaceAll("/n", "").replaceAll(" ", "").replaceAll("=3D", "=");

                            StringBuilder st = new StringBuilder(word);
                            st.deleteCharAt(st.length()-1);

                            completeUrl.append(st);

                        } else if (completeUrl != null){
                            if (line_string.contains(" ") || line_string.contains("<") || line_string.contains("/r") || line_string.contains("/n")) {
                                String line = line_string.replaceAll(" ", "<");

                                completeUrl.append(line.split("<")[0]);
                                extractURL(completeUrl.toString());

                                completeUrl = null;
                            } else {
                                line_string = line_string.replaceAll("=3D", "=");

                                if (line_string.trim().substring(line_string.length()-1).equalsIgnoreCase("=")) {
                                    StringBuilder st = new StringBuilder(line_string);
                                    st.deleteCharAt(st.length()-1);
                                    completeUrl.append(st);
                                } else {

                                    completeUrl.append(line_string);
                                }
                            }
                        }

                        if (line_string.contains(" ") || line_string.contains("<") || line_string.contains("/r") || line_string.contains("/n")) {
                            if (completeUrl == null) {
                                extractURL(line_string);
                            }
                        }

                        if (check.length() > 2 && substring.equalsIgnoreCase("=")) {
                            StringBuilder st = new StringBuilder(line_string);
                            st.deleteCharAt(st.length()-1);
                            line_string = st.toString();
                        }

                        message.append(line_string);

                    }
                } else {
                    if (line_string.contains(boundary)) {
                        reading = true;
                    }
                }
            } else if (line_string.contains("boundary")) {
                boundary = line_string.split("boundary=")[1].replaceAll("\"", "");
                end_boundary = boundary + "--";
            }
        }
        return message.toString();
    }

    /**
     * Method to extract URLs from messages
     * @param line Line to check
     */
    private void extractURL(String line) {
        boolean hidden = line.contains("<") && line.contains(">");

        line = line.replaceAll(">", " ").replaceAll("<", " ").replaceAll("=20", "");

        for (String word : line.split(" ")) {
            if (word.contains("href")) {
                word = word.replaceAll("href=3D", "");
                word = word.replaceAll("href=", "");
                word = word.replaceAll("\"", "");
                hidden = true;
            }

            try {
                new URL(word);
                if ((word.contains("https:=")) || (word.contains("mailto:")) || (word.contains("tlf:"))) {
                    continue;
                }

                word = word.replaceAll(" ", "");

                if (!urls.contains(word)) {
                    urls.add(word);
                    if (hidden && !word.contains("ct.sendgrid.net")) {
                        this.hidden.add(word);
                    }
                }

            } catch (Exception ignored) {}
        }
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ArrayList<String> getHidden() {
        return hidden;
    }

    public void addURLs(ArrayList<String> copy) {
        for (String url: copy) {
            if(!this.urls.contains(url)) {
                this.urls.add(url);
            }
        }
    }

    public void clear() {
        this.urls.clear();
        this.hidden.clear();
    }
}
