package com.example.backend.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    public static String sanitize(String dirtyHtml) {
        return Jsoup.clean(dirtyHtml, Safelist.basic());
    }
}

