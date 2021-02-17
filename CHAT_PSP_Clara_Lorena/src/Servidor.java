import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class Servidor extends JFrame implements Runnable{

	private JPanel contentPane;
	static JTextArea texto;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor frame = new Servidor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Servidor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 433, 456);
		setTitle("CHAT");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
	
		texto = new JTextArea();
		panel.add(texto, BorderLayout.CENTER);
		add(panel);
		
		setVisible(true);
		Thread hilo = new Thread(this);
		hilo.start();
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			ServerSocket serverSocket = new ServerSocket(9999);
			String nombre, ip, mensaje;
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				Socket socket = serverSocket.accept();
				
				InetAddress address = socket.getInetAddress();
				
				String ipRemota = address.getHostAddress();
				
				ObjectInputStream data = new ObjectInputStream(socket.getInputStream());
				paqueteRecibido = (PaqueteEnvio) data.readObject();
				
				nombre = paqueteRecibido.getNombre();
				ip = paqueteRecibido.getIp();
				mensaje = paqueteRecibido.getMensaje();
				
				texto.append("\n" + nombre + ": " + mensaje + " para " + ip);
				
				Socket enviar = new Socket(ip, 9090);
				ObjectOutputStream reenvio = new ObjectOutputStream(enviar.getOutputStream());
				reenvio.writeObject(paqueteRecibido);
				
				enviar.close();
				socket.close();
				reenvio.close();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
