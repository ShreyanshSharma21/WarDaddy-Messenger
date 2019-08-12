import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server()
	{
		super("WarDaddy Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
							sendMessage(e.getActionCommand());
							userText.setText("");
					}
				});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(500,500);
		setVisible(true);
		
	}
	
	public void startRunning()
	{
		try
		{
			server = new  ServerSocket(6789 , 100);
			while(true)
			{
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofException) {
					showMessage("\n server fucked you");
				}finally {
					closeCrap();
				}
			}
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	
	//wait for connection , then display connection information
	private void waitForConnection()throws IOException
	{
		showMessage("Waiting for someone to connect ... \n");
		connection = server.accept();
		showMessage("Now Conncected to " +  connection.getInetAddress().getHostName());
		
		
		
	}
	
	private void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n streams are now set up");
		
	}
	
	private void whileChatting() throws IOException
	{
		String message = "You are now Fuckin another person";
		sendMessage(message);
		ableTOType(true);
		do
		{
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
				
			}catch(ClassNotFoundException classNotFoundException)
			{
				showMessage("\n i dunno what the fuck user sent");
			}
			
		}while(!message.equals("CLIENT - END"));
		
	}
	
	private void closeCrap()
	{
		showMessage("\n shutting the fuck up AKA closing connections");
		ableTOType(false);
		
		try {
			output.close();
			input.close();
			connection.close();
			
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(String message)
	{
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException) {
			
			chatWindow.append("\n ERRoR: I can't send that message asshole");
			
			
		}
		
	}
	
	//updates chat window
	private void showMessage(final String text)
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					
					@Override
					public void run() {
						chatWindow.append(text);
						}
				});
		
	}
	
	//let the user type on his turn
	private  void  ableTOType(final boolean tof) 
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					
					@Override
					public void run() {
						userText.setEditable(tof);
						}
				});
	}
	
}
