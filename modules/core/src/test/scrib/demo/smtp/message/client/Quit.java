package demo.smtp.message.client;

import demo.smtp.Smtp.Smtp.Smtp;
import demo.smtp.message.SmtpMessage;

public class Quit extends SmtpMessage
{
	private static final long serialVersionUID = 1L;

	public Quit()
	{
		super(Smtp.Quit);
	}
}