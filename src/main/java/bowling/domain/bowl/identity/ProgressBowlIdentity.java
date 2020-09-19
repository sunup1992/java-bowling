package bowling.domain.bowl.identity;

import bowling.domain.bowl.BowlResult;
import bowling.domain.frame.Frame;
import bowling.domain.frame.NormalFrame;

import java.text.MessageFormat;

import static bowling.domain.NumberOfPin.MAX_NUMBER_OF_PIN;

public class ProgressBowlIdentity extends AbstractBowlIdentity {

    public static final String PROGRESS = "{0}";

    @Override
    public boolean identity(BowlResult bowlResult) {
        return bowlResult.getBowlCount() == FIRST_BOWL &&
                bowlResult.getTotalNumberOfPin() < MAX_NUMBER_OF_PIN;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public boolean isBonus() {
        return false;
    }

    @Override
    public int getScore(Frame frame) {
        return ((NormalFrame) frame).getTotalNumberOfPin();
    }

    @Override
    public String message(BowlResult bowlResult) {
        return MessageFormat.format(PROGRESS, bowlResult.getFirstNumberOfPin());
    }

}
