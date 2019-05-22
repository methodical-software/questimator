package questimator;

import java.util.ArrayList;
import java.util.List;

public class MCQ {
    private String question;
    private List<String> options = new ArrayList<>();

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }
}
