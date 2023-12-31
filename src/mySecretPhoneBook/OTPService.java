package mySecretPhoneBook;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Random;

public class OTPService extends Service<String> {
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        number = new Random().nextInt(501);
                        System.out.println("Il numero è : " + number);
                        updateValue(number.toString());
                        Thread.sleep(10000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return number.toString();
                }
                return number.toString();
            }
        };
    }
}
