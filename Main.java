import java.util.*;
import ATMDispenseChain;  
class ATM
{
private boolean userAuthenticated;              // whether user is authenticated
private int currentAccountNumber;               // current user's account number
private Screen screen;                           // ATM's screen
private Keypad keypad;                           // ATM's keypad
private CashDispenser cashDispenser;           
private DepositSlot depositSlot;                  
private BankDatabase bankDatabase;               

private static final int BALANCE_INQUIRY = 1;
private static final int WITHDRAWAL = 2;
private static final int DEPOSIT = 3;
private static final int EXIT = 4;

ATM()                                                   // Constructor
{
userAuthenticated = false;                   
currentAccountNumber = 0;                     
screen = new Screen();                       
keypad = new Keypad();                           
cashDispenser = new CashDispenser();           
depositSlot = new DepositSlot();            
bankDatabase = new BankDatabase();              
}
public void run()
{
while ( true )
{
while ( !userAuthenticated )
{
    screen.displayMessageLine( "\nWelcome!" );
    authenticateUser();             // authenticate user  [function]
} 
performTransactions();              // user is now authenticated   [function]
userAuthenticated = false;          // reset before next ATM session
currentAccountNumber = 0;           // reset before next ATM session
screen.displayMessageLine( "\nThank you! Goodbye!" );
} 
} 

private void authenticateUser()
{
screen.displayMessage("\nPlease enter your account number: ");
int accountNumber = keypad.getInput(); 
screen.displayMessage("\nEnter your PIN: " ); 
int pin = keypad.getInput();
userAuthenticated = bankDatabase.authenticateUser(accountNumber, pin );
if ( userAuthenticated )
currentAccountNumber = accountNumber;                       // save user's account #
else
screen.displayMessageLine("Invalid account number OR PIN. Piease try again." );
}

private void performTransactions()
{
Transaction currentTransaction = null;
boolean userExited = false;                   
while ( !userExited )
{
int mainMenuSelection = displayMainMenu();       // return int value 
switch(mainMenuSelection )
{
case BALANCE_INQUIRY:
case WITHDRAWAL:
case DEPOSIT:
currentTransaction =createTransaction(mainMenuSelection );
currentTransaction.execute(); 
break;

case EXIT: 
screen.displayMessageLine( "\nExiting the system..." );
userExited = true; 
break;

default:
screen.displayMessageLine("\nYou did not enter a valid selection. Try again." );
break;
} 
} 
} 

private int displayMainMenu()
{
screen.displayMessageLine( "\nMain menu " );
screen.displayMessageLine( "l — View my balance" );
screen.displayMessageLine( "2 — Withdraw cash" );
screen.displayMessageLine( "3 — Deposit cash" );
screen.displayMessageLine( "4 — Exit\n" );
screen.displayMessage( "Enter a choice: " );
return keypad.getInput(); 
}

private Transaction createTransaction( int type )
{
Transaction temp = null;
switch ( type )
{
case BALANCE_INQUIRY: 
temp = new BalanceInquiry(currentAccountNumber, screen, bankDatabase );
break;

case WITHDRAWAL: 
temp = new Withdrawal( currentAccountNumber, screen,bankDatabase, keypad, cashDispenser );
break;

case DEPOSIT: 
temp = new Deposit( currentAccountNumber, screen, bankDatabase, keypad, depositSlot );
break;
} 
return temp; 
} }

//// Class
class Screen
{
public void displayMessage( String message )
{
    System.out.print( message );
}
public void displayMessageLine( String message )
{
    System.out.println( message );
}  
public void displayAmount(double amount )
{
System.out.printf( "(INR) ₹ %,.2f", amount );
} } 

//// Class
class Keypad
{
private Scanner input; 
public Keypad()
{
input = new Scanner( System.in );
}
public int getInput()
{
return input.nextInt(); // we assume that user enters an integer
} } 

//// Class
class CashDispenser
{
private final static int INITIAL_COUNT = 500;
private int count; 
public CashDispenser()
{
count = INITIAL_COUNT; 
} 
public void dispenseCash( int amount )
{
int billsRequired = amount / 20; 
count =count - billsRequired;  
} 
public boolean isSufficientCashAvailable( int amount )
{
int billsRequired = amount / 20; 
if ( count >= billsRequired )
return true;
else
return false;
} } 
//// Class
class DepositSlot
{
public boolean isEnvelopeReceived()
{
return true;
}  } 

//// Class
class Account
{
 private int accountNumber; 
 private int pin; 
 private double availableBalance; 
 private double totalBalance;
 
 public Account( int theAccountNumber, int thePIN, double theAvailableBalance, double theTotalBalance )
{
 accountNumber = theAccountNumber;
 pin = thePIN;
 availableBalance = theAvailableBalance;
 totalBalance = theTotalBalance;
} 
public boolean validatePIN( int userPIN )
{
if ( userPIN == pin )
return true;
else
return false;
} 
public double getAvailableBalance()
{
return availableBalance;
}
public double getTotalBalance()
{
return totalBalance;
} 
public void credit( double amount )
{
totalBalance = totalBalance+ amount;
} 

public void debit( double amount )
{
availableBalance -= amount;  
totalBalance -= amount;  
} 
public int getAccountNumber()
{
return accountNumber;
} } 

//// Class
class BankDatabase
{
private Account[] accounts; // array of Accounts
public BankDatabase()
{
accounts = new Account[ 2 ]; // just 2 accounts for testing
accounts[ 0 ] = new Account( 12345, 54321, 10000.0, 12000.0 );
accounts[ 1 ] = new Account( 98765, 56789, 19000.0, 20000.0 );
} 
private Account getAccount( int accountNumber )
{
    for ( Account currentAccount : accounts )
{
if ( currentAccount.getAccountNumber() == accountNumber )
return currentAccount;
} 
return null; 
} 
public boolean authenticateUser( int userAccountNumber, int userPIN )
{
Account userAccount = getAccount( userAccountNumber );
if ( userAccount != null )
return userAccount.validatePIN( userPIN );
else
return false; 
}

public double getAvailableBalance( int userAccountNumber )
{
return getAccount( userAccountNumber ).getAvailableBalance();
} 
public  double getTotalBalance( int userAccountNumber )
{
return getAccount( userAccountNumber ).getTotalBalance();
} 
public void credit( int userAccountNumber, double amount )
{
getAccount( userAccountNumber ).credit( amount );
} 
public void debit( int userAccountNumber, double amount )
{
getAccount( userAccountNumber ).debit( amount );
} } 

//// Class
abstract class Transaction
{
private int accountNumber; 
private Screen screen; 
private BankDatabase bankDatabase; 
public Transaction( int userAccountNumber, Screen atmscreen,BankDatabase atmBankDatabase )
{
accountNumber = userAccountNumber;
screen = atmscreen;
bankDatabase = atmBankDatabase;
} 
public int getAccountNumber()
{
return accountNumber;
} 
public Screen getScreen()
{  return screen; } 
public BankDatabase getBankDatabase()
{ return bankDatabase; } 
abstract public void execute();
} 

//// Class
class BalanceInquiry extends Transaction
{
public BalanceInquiry(int userAccountNumber, Screen atmscreen,BankDatabase atmBankDatabase )
{
super( userAccountNumber, atmscreen, atmBankDatabase );
} 
@Override
public void execute()
{
BankDatabase bankDatabase = getBankDatabase();
Screen screen = getScreen();
double availableBalance =bankDatabase.getAvailableBalance(getAccountNumber() );
 double totalBalance = bankDatabase.getTotalBalance( getAccountNumber() );
 screen.displayMessageLine( "\nBalance Information" );
 screen.displayMessage( " — Available ba1ance: " );
 screen.displayAmount(availableBalance );
 screen.displayMessage( "\n — Total balance: " );
 screen.displayAmount( totalBalance );
 screen.displayMessageLine( "  " );
}  } 

//// Class
class Withdrawal extends Transaction
{
private int amount;  
private Keypad keypad;                          // reference variable
private CashDispenser cashDispenser;            // reference variable
private final static int CANCELED = 1;
public Withdrawal( int userAccountNumber, Screen atmscreen,BankDatabase atmBankDatabase, Keypad atmKeypad,CashDispenser atmChashDispenser )
{
super( userAccountNumber, atmscreen, atmBankDatabase );
keypad = atmKeypad;
cashDispenser = atmChashDispenser;
}  
@Override
public void execute()
{
boolean cashDispensed = false; 
double availableBalance;
BankDatabase bankDatabase = getBankDatabase();
Screen screen = getScreen();
do
{
amount = displayMenuOfAmounts();
if ( amount != CANCELED )
{
availableBalance  =bankDatabase.getAvailableBalance(getAccountNumber() );
if ( amount <= availableBalance )
{
if ( cashDispenser.isSufficientCashAvailable( amount ) )
{
bankDatabase.debit( getAccountNumber(), amount );
cashDispenser.dispenseCash( amount );
cashDispensed = true;  
screen.displayMessageLine("\nYour cash has been" +" dispensed. Piease take your cash now." );
} 
else 
screen.displayMessageLine("\nInsufficient cash available in the ATM." +"\n\nPlease choose a sma11er amount." );
} 
else 
{
screen.displayMessageLine("\nInsufficient cash in your account." +"\n\nP1ease choose a sma11er amount." );
} } 
else 
{
screen.displayMessageLine( "\nCance1ing transaction..." );
return;
} } 
while ( !cashDispensed );
} 

private int displayMenuOfAmounts()
{
int userChoice = 0; 
Screen screen = getScreen(); 
while ( userChoice == 0 )
{
screen.displayMessageLine("1 — Cancel transaction" );
screen.displayMessageLine("2 — Confirm transaction: " );
int input = keypad.getInput();
 switch ( input )
{
case CANCELED: 
userChoice = CANCELED; 
break;
case 2:
    screen.displayMessageLine("\nEnter a withdrawal amount to dispense: " );
    ATMDispenseChain  obj = new ATMDispenseChain();
			int Amount = 0;
			Amount = keypad.getInput();
			if (Amount % 100 != 0) 
			{
				System.out.println("Amount should be in multiple of 100s.");
				return;
			}
	obj.c1.dispense(new Currency(amount));
break;
default:                                 
screen.displayMessageLine("\nInvalid selection. Try again." );
}
} 
return Amount; 
}  } 

//// Class
class Deposit extends Transaction
{
private double amount; 
private Keypad keypad; 
private DepositSlot depositSlot; 
private final static int CANCELED = 0; 
public Deposit( int userAccountNumber, Screen atmscreen,BankDatabase atmBankDatabase, Keypad atmKeypad,DepositSlot atmDepositSlot )
{
super( userAccountNumber, atmscreen, atmBankDatabase );
keypad = atmKeypad;
depositSlot = atmDepositSlot;
}  
@Override
public void execute()
{
BankDatabase bankDatabase = getBankDatabase();              // get reference
Screen screen = getScreen();                                // get reference
amount = promptForDepositAmount();  
if ( amount != CANCELED )
{
screen.displayMessage("\nPlease insert a deposit envelope containing " );
screen.displayAmount(amount );
screen.displayMessageLine( ".");
boolean envelopeReceived = depositSlot.isEnvelopeReceived();
if ( envelopeReceived )
{
    screen.displayMessageLine( "\nYour Cash has been " +"recieved \nNote:The money just deposited Will not be avaliabie until we verify the amount of any " + "enclosed cash and your checks clear." );
    bankDatabase.credit( getAccountNumber(), amount );
} 
else 
screen.displayMessageLine( "\nYou did not insert an " +"envelope, so the ATM has canceled your transaction." );
} 
else 
screen.displayMessageLine("\nCanceling transaction..." );
} 
private double promptForDepositAmount()
{
Screen screen = getScreen();                        // reference variable
screen.displayMessageLine("\nDeposit Menu:");
screen.displayMessageLine("1 - ₹100" );
screen.displayMessageLine("2 — ₹200" );
screen.displayMessageLine("3 — ₹500" );
screen.displayMessageLine("4 — ₹2000" );
screen.displayMessage("\nPlease enter a deposit amount in " + "(INR ₹) (Or 0 to cancel): " );
int input = keypad.getInput(); 
if ( input == CANCELED )
return CANCELED;
else
return ( double ) input; 
}  }  

//// Class
public class Main
{
public static void main( String[] args )
{
ATM obj = new ATM();
obj.run();
} 
} 

