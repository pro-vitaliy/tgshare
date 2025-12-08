package com.github.provitaliy.node.bot;

public class BotResponse {
    public static final String START_RESPONSE = "Добро пожаловать! Чтобы посмотреть список доступных команд "
                                                + "введите " + ServiceCommand.HELP;

    public static final String UNKNOWN_RESPONSE = "Неизвестная команда! Чтобы посмотреть список доступных команд "
                                                  + "введите " + ServiceCommand.HELP;

    public static final String HELP_RESPONSE = String.format("""
            Список доступных команд:
            %s - отмена выполнения текущей команды;
            %s - регистрация пользователя.
            """, ServiceCommand.CANCEL, ServiceCommand.REGISTRATION
    );

    public static final String CANCEL_RESPONSE = "Команда успешно отменена";
    public static final String WAIT_FOR_EMAIL_RESPONSE = "Введите ваш email";
    public static final String ALREADY_REGISTERED_RESPONSE = "Вы уже зарегистрированы!";
    public static final String EMAIL_CONFIRMATION_RESPONSE = "Пройдите по ссылке в письме для завершения регистрации";
    public static final String INCORRECT_EMAIL_ANSWER = "Введен некорректный email. Попробуйте еще раз или "
                                                        + "введите %s для отмены.".formatted(ServiceCommand.CANCEL);
    public static final String NOT_ALLOW_TO_SEND_FILE_RESPONSE = "Зарегистрируйтесь для отправки контента с"
                                                                 + " помощью команды " + ServiceCommand.REGISTRATION;
    public static final String FILE_RECEIVED_RESPONSE = "Файл получен. Ссылка для скачивания скоро будет готова.";
    public static final String USER_ACTIVATED_RESPONSE = "Ваш email успешно подтвержден, учетная запись активирована!";
    public static final String FILE_READY_RESPONSE = "Файл успешно загружен и доступен по ссылке: %s";
    public static final String EMAIL_ALREADY_EXIST = "Email %s уже занят. Пожалуйста, используйте другой email.";
    public static final String FILE_UPLOAD_FAILURE_RESPONSE = "Не удалось загрузить файл %s. Пожалуйста, попробуйте еще раз.";


}
