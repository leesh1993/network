package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer {
	private static final int PORT = 5000;
	
	public static void main(String[] args) {		
		ServerSocket serverSocket = null;
			
		try {
			//1. 서버소켓 생성
			serverSocket = new ServerSocket();
			
			//2. Binding : Socket에 SocketAddress(IPAddress + Port) 바인딩한다.
			InetAddress inetAddress = InetAddress.getLocalHost();
			InetSocketAddress inetSocketAddress = 
					new InetSocketAddress(inetAddress,PORT);			
			serverSocket.bind(inetSocketAddress);		
			System.out.println("[TCPServer] binding " + inetAddress.getHostAddress() + ":" + PORT);
			
			//3. accept : 클라이언트로부터 연결요청(Connect)을 기다린다.
			Socket socket = serverSocket.accept(); // Blocking
			
			//상대편 Address, Port 얻어오기
			InetSocketAddress inetRemoteSocketAddress = 
					(InetSocketAddress)socket.getRemoteSocketAddress();//다운케스팅..			
			String remoteHostAddress = 
					inetRemoteSocketAddress.getAddress().getHostAddress(); 			
			int remoteHostPort = inetRemoteSocketAddress.getPort();		
			System.out.println("[TCPServer] connected from client " + remoteHostAddress + ":" + remoteHostPort);
			
			try {
				//4. IOStream 받아오기
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				
				while(true) {
					//5. 데이터 읽기
					byte[] buffer = new byte[256];
					int readByteCount = is.read(buffer); // 크기가 항상 같지 않으므로 체크 (blocking)
					
					//정상종료 : remote socket이 close()
					//메소드를 통해서 정상적으로 소켓을 닫는다.
					
					//count가 -1일 경우 클라이언트가 연결을 끊은거다
					if(readByteCount == -1) {
						System.out.println("[TCPServer] closed by client");
						break;
					}
					
					//encording(InputStreamReader 대체)
					String data = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.println("[TCPServer] received :" + data );
					
					//6. 데이터 쓰기
					os.write(data.getBytes("UTF-8"));
				}
			
			} catch (SocketException e) {
				System.out.println("[TCPServer] abnormal closed by client");
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				//7. data socket 자원정리
				if(socket != null && socket.isClosed() == false)
					socket.close();
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//8. serverSocket 자원정리
				if(serverSocket != null && serverSocket.isClosed() == false)
					serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
