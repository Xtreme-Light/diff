package org.example;


public record DiffPair<T>(String path, T left, T right) {
}
