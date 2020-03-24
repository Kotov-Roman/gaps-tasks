package accounts;

import java.util.concurrent.atomic.AtomicLong;

public class Account {

  private AtomicLong account;

  public Account(long amount) {
        account = new AtomicLong(amount);
  }

  public void addMoney(long amount) {
    account.addAndGet(amount);
  }

  public synchronized boolean tryToTakeMoney(long amount) {
    if (account.longValue()>amount){
      account.addAndGet(-amount);
      return true;
    }
    return false;
  }

  public AtomicLong getAccount() {
    return account;
  }

  public void setAccount(AtomicLong account) {
    this.account = account;
  }
}
