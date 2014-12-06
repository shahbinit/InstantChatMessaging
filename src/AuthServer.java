import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Statement;

public class AuthServer {
	String serverAdd;
	String sqlHost;
	String sqlUsername;
	String sqlPassword;
	int PORT = 8000;
	DatagramSocket ds;

	public AuthServer(String serverAdd, String sqlHost, String sqlUsername,
			String sqlPassword) throws SocketException {
		this.serverAdd = serverAdd;
		this.sqlHost = sqlHost;
		this.sqlPassword = sqlPassword;
		this.sqlUsername = sqlUsername;
		ds = new DatagramSocket(PORT);
	}

	public String genSHA256(String original) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(original.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}

		return sb.toString();
	}

	public boolean authUser(String user, String pass)
			throws ClassNotFoundException, SQLException {
		Connection connect = null;
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean found = false;
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager
				.getConnection("jdbc:mysql://" + sqlHost + "/test?" + "user="
						+ sqlUsername + "&password=" + sqlPassword);
		statement = connect.createStatement();
		resultSet = statement
				.executeQuery("select count(1) as cnt from chat.userinfo where username='"
						+ user + "'");
		while (resultSet.next()) {

			if (resultSet.getInt("cnt") == 1)
				found = true;
		}
		return found;
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, NoSuchAlgorithmException, SocketException {

		String host = "localhost:3307";
		String uname = "root";
		String pass = "";
		String server = "";
		if (args.length == 4) {
			host = args[1];
			uname = args[2];
			pass = args[3];
		}
		if (args.length > 1 && args.length < 4)
			usage();
		AuthServer as = new AuthServer(server, host, uname, pass);
		if (as.authUser("binit", "1630937c3d00b4f4b153599d93469963"))
			System.out.println("found binit");
		System.out.println(as.genSHA256("sadhvani"));
	}

	/**
	 * Print usage message
	 */
	public static void usage() {
		System.err.println("USAGE:");
		System.err
				.println("java AuthServer serverAddress sqlhost sqlusername sqlpassword");
		System.err
				.println("OR\njava AuthServer serverAddress\n(default values are taken for the rest)");

		System.err.println("OR\njava AuthServer\n(default values are taken)");
		throw new IllegalArgumentException();
	}

	public void run() {

		while (true) {
			
		}
	}
}