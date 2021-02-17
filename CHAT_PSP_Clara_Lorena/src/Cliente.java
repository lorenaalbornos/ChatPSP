import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class Cliente extends JFrame implements Runnable{

	private JPanel contentPane;
	static JTextField txtMensaje;
	static JTextField txNombre;
	static JTextField txPuerto;
	static JTextArea textPanel = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
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
	public Cliente() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 433, 456);
		setTitle("CHAT");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtMensaje = new JTextField();
		txtMensaje.setBounds(23, 349, 286, 42);
		contentPane.add(txtMensaje);
		txtMensaje.setColumns(10);
		
		JButton btnEnviar = new JButton("Enviar");
		EnviarTexto texto = new EnviarTexto();
		btnEnviar.addActionListener(texto);
		btnEnviar.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnEnviar.setBounds(322, 349, 69, 42);
		contentPane.add(btnEnviar);

		Thread hilo = new Thread(this);
		hilo.start();
		
		txNombre = new JTextField();
		txNombre.setBounds(79, 12, 86, 20);
		contentPane.add(txNombre);
		txNombre.setColumns(10);

		JLabel lblNewLabel = new JLabel("Nombre");
		lblNewLabel.setBounds(23, 14, 46, 14);
		contentPane.add(lblNewLabel);
		JLabel lblPuerto = new JLabel("Puerto");
		lblPuerto.setBounds(231, 14, 46, 14);
		contentPane.add(lblPuerto);

		txPuerto = new JTextField();
		txPuerto.setColumns(10);
		txPuerto.setBounds(287, 11, 86, 20);
		contentPane.add(txPuerto);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 64, 368, 268);
		contentPane.add(scrollPane);

		textPanel = new JTextArea();
		scrollPane.setViewportView(textPanel);
		
		addWindowListener(new EnvioOnline());
	}

	@Override
	public void run() {
		try {
			ServerSocket servidorCliente = new ServerSocket(9090);
			Socket cliente;
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				cliente = servidorCliente.accept();
				ObjectInputStream flujo = new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido = (PaqueteEnvio) flujo.readObject();
				textPanel.append("\n" + paqueteRecibido.getNombre() + ": " + paqueteRecibido.getMensaje());
				
				cliente.close();
				flujo.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e) {
		
		try {
			
			Socket socket = new Socket("192.168.1.129", 9999);
			PaqueteEnvio paqueteEnvio = new PaqueteEnvio();
			paqueteEnvio.setMensaje(" Online");
			
			ObjectOutputStream paquete = new ObjectOutputStream(socket.getOutputStream());
			paquete.writeObject(paqueteEnvio);
			
			socket.close();
			
		} catch ( Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}

class EnviarTexto implements ActionListener {


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		Cliente.textPanel.append("\n" + "Yo: " + Cliente.txtMensaje.getText());
		try {
			String address = InetAddress.getLocalHost().getHostAddress();
			Socket socket = new Socket("192.168.1.129", 9999);
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setNombre(Cliente.txNombre.getText());
			datos.setIp(Cliente.txPuerto.getText());
			datos.setMensaje(Cliente.txtMensaje.getText());
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(socket.getOutputStream());
			paqueteDatos.writeObject(datos);
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class PaqueteEnvio implements Serializable {
	private String nombre;
	private String ip;
	private String mensaje;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
