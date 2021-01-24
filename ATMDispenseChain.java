import java.util.*;
// Interface
interface DispenseChain 
{
	void setNextChain(DispenseChain nextChain);
	void dispense(Currency cur);
}
// Class
class Currency 
{	private int amount;
	
	public Currency(int amt)
	{
		this.amount=amt;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
}
// Class
class INR_100_Dispenser implements DispenseChain 
{
	private DispenseChain chain;
	@Override
	public void setNextChain(DispenseChain nextChain) 
	{
		this.chain=nextChain;
	}
	@Override
	public void dispense(Currency cur) 
	{
		if(cur.getAmount() >= 100)
		{
			int num = cur.getAmount()/100;
			int remainder = cur.getAmount() % 100;
			System.out.println("Dispensing "+num+" 100₹  note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}
		else
		this.chain.dispense(cur);
	}

}
// Class
class INR_200_Dispenser implements DispenseChain
{
	private DispenseChain chain;
	@Override
	public void setNextChain(DispenseChain nextChain) 
	{
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) 
	{
		if(cur.getAmount() >= 200){
			int num = cur.getAmount()/200;
			int remainder = cur.getAmount() % 200;
			System.out.println("Dispensing "+num+" 200 ₹  note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}
		else{
			this.chain.dispense(cur);
		}
	}

}
// Class
class INR_500_Dispenser implements DispenseChain 
{

	private DispenseChain chain;
	
	@Override
	public void setNextChain(DispenseChain nextChain) 
	{
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) 
	{
		if(cur.getAmount() >= 500){
			int num = cur.getAmount()/500;
			int remainder = cur.getAmount() % 500;
			System.out.println("Dispensing "+num+" 500$ note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}else{
			this.chain.dispense(cur);
		}
	}

}
// Class
class INR_2000_Dispenser implements DispenseChain 
{

	private DispenseChain chain;
	
	@Override
	public void setNextChain(DispenseChain nextChain) 
	{
		this.chain=nextChain;
	}

	@Override
	public void dispense(Currency cur) 
	{
		if(cur.getAmount() >= 2000){
			int num = cur.getAmount()/2000;
			int remainder = cur.getAmount() % 2000;
			System.out.println("Dispensing "+num+" 2000$ note");
			if(remainder !=0) this.chain.dispense(new Currency(remainder));
		}else{
			this.chain.dispense(cur);
		}
	}

}
// Class
public class ATMDispenseChain 
{
	private DispenseChain c1;
	public ATMDispenseChain() 
	{

		this.c1 = new INR_2000_Dispenser();
		DispenseChain c2 = new INR_500_Dispenser();
		DispenseChain c3 = new INR_200_Dispenser();
        DispenseChain c4 = new INR_100_Dispenser();

		c1.setNextChain(c2);
		c2.setNextChain(c3);
		c3.setNextChain(c4);
		
	}

	public static void main(String[] args) {
		ATMDispenseChain  obj = new ATMDispenseChain ();
		while (true)
		{
			int amount = 0;
			System.out.println("Enter amount to dispense");
			Scanner input = new Scanner(System.in);
			amount = input.nextInt();
			if (amount % 100 != 0) 
			{
				System.out.println("Amount should be in multiple of 100s.");
				return;
			}
		obj.c1.dispense(new Currency(amount));
		}

	}

}
