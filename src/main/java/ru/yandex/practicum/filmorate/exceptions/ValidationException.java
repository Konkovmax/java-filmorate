package ru.yandex.practicum.filmorate.exceptions;

//возможно тут надо наследоваться от другого исключения, но не нашёл от какого лучше
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
