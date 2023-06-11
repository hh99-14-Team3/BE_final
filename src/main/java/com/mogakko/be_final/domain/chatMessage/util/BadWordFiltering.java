package com.mogakko.be_final.domain.chatMessage.util;


import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class BadWordFiltering implements BadWords {
    private final Set<String> set = new HashSet<>(List.of(badWords));

    public ChatMessage checkBadWord(ChatMessage chatMessage) {
        String text = chatMessage.getMessage();
        StringBuilder singBuilder = new StringBuilder("[");
        for (String sing : sings) singBuilder.append(Pattern.quote(sing));
        singBuilder.append("]*");
        String patternText = singBuilder.toString();

        for (String word : set) {
            if (word.length() == 1) text = text.replace(word, substituteValue);
            String[] chars = word.split("");
            text = Pattern.compile(String.join(patternText, chars))
                    .matcher(text)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }
        chatMessage.setMessage(text);

        return chatMessage;
    }
}
