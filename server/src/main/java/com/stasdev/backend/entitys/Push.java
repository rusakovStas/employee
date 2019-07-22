package com.stasdev.backend.entitys;

import java.util.Objects;

public class Push {
    private String content;

    public String getContent() {
        return content;
    }

    public Push setContent(String content) {
        this.content = content;
        return this;
    }

    public Push() {
    }

    public Push(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Push)) return false;
        Push push = (Push) o;
        return Objects.equals(getContent(), push.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent());
    }
}
