package com.mogakko.be_final.util;


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

        // 문자열을 빌더 패턴을 사용하여 생성
        StringBuilder stringBuilder = new StringBuilder("[");
        for (String sing : sings) stringBuilder.append(Pattern.quote(sing));
        stringBuilder.append("]*");
        String patternText = stringBuilder.toString();

        // set에 있는 각 단어에 대해 필터링 작업을 수행
        for (String word : set) {
            // 단어의 길이가 1이면 단어를 대체값(substituteValue)으로 치환
            if (word.length() == 1) text = text.replace(word, substituteValue);

            // 단어를 문자 단위로 분할해서 배열에 저장
            String[] chars = word.split("");

            // 패턴을 생성하여 문자 단위로 일치하는 부분을 대체값으로 치환
            text = Pattern.compile(String.join(patternText, chars))
                    .matcher(text)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }
        chatMessage.setMessage(text);
        return chatMessage;
    }

    public String checkBadWordString(String inputString) {
        // 문자열을 빌더 패턴을 사용하여 생성
        StringBuilder stringBuilder = new StringBuilder("[");
        for (String sing : sings) stringBuilder.append(Pattern.quote(sing));
        stringBuilder.append("]*");
        String patternText = stringBuilder.toString();

        // set에 있는 각 단어에 대해 필터링 작업을 수행
        for (String word : set) {
            // 단어의 길이가 1이면 단어를 대체값(substituteValue)으로 치환
            if (word.length() == 1) inputString = inputString.replace(word, substituteValue);

            // 단어를 문자 단위로 분할해서 배열에 저장
            String[] chars = word.split("");

            // 패턴을 생성하여 문자 단위로 일치하는 부분을 대체값으로 치환
            inputString = Pattern.compile(String.join(patternText, chars))
                    .matcher(inputString)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }
        return inputString;
    }
}
