/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class ApplicationTest {
    private static ExpenseManager expenseManager;
    private String accountNo = "12345";
    private String bankName = "BOC";
    private String accountHolderName = "KASUN";

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        expenseManager = new PersistentExpenseManager(context);
        double balance = 5000;
        expenseManager.addAccount(accountNo, bankName, accountHolderName, balance);
    }


    @Test
    public void addAccount() {
        List<String> accountNumber = expenseManager.getAccountNumbersList();
        assertTrue(accountNumber.contains("12345"));
    }

    @Test
    public void addTransaction() throws InvalidAccountException {
        int day = 20;
        int month = 5;
        int year = 2022;
        ExpenseType expenseType = ExpenseType.INCOME;
        String amount = "500";

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date transactionDate = calendar.getTime();

        int transactionsOldSize = expenseManager.getTransactionLogs().size();

        expenseManager.updateAccountBalance(accountNo, day, month, year, expenseType, amount);

        List<Transaction> transactions = expenseManager.getTransactionLogs();

        boolean success = false;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (Transaction transaction : transactions) {
            boolean dateMatched = fmt.format(transactionDate).equals(fmt.format(transaction.getDate()));
            if (transaction.getAccountNo().equals(accountNo) && dateMatched && transaction.getExpenseType() == expenseType && transaction.getAmount() == Double.valueOf(amount)) {
                success = true;
                break;
            }
        }

        assertTrue(success && transactions.size() > transactionsOldSize);
    }
}


