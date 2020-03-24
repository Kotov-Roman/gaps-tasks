package accounts;

import java.util.ArrayList;
import java.util.List;

public class Transferor implements Runnable {

  private List<Account> accounts;
  private Account debit;
  private Account credit;
  private int change ;

  public Transferor(List<Account> accounts) {
    this.accounts = accounts;
  }

  public void run() {

    change = (int) (Math.random() * 350);
    System.err.println(Thread.currentThread().getName() + " is running");
    for (int i = 0; i < 1000000; i++) {
      setRandomAccounts();
      boolean isTaken = debit.tryToTakeMoney(change);
      if (isTaken) {
        credit.addMoney(change);
      }
    }
    System.err.println(Thread.currentThread().getName() + " is finished");
  }

  private void setRandomAccounts() {
    int randomIndex = (int) (Math.random() * accounts.size());
    debit = accounts.get(randomIndex);
    randomIndex = (int) (Math.random() * accounts.size());
    credit = accounts.get(randomIndex);
  }

}
