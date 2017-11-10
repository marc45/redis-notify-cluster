package com.hdkj.notify.advance;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.hdkj.notify.util.ServerConfig;

/**
 * 关于nio selector 的使用实践 以及 对于指定客户端发送数据实践 在Java IO中，基本上可以分为文件类和Stream类两大类。
 * Channel 也相应地分为了FileChannel 和 Socket Channel， 其中 socket channel
 * 又分为三大类，一个是用于监听端口的ServerSocketChannel，
 * 第二类是用于TCP通信的SocketChannel，第三类是用于UDP通信的DatagramChannel
 * 
 * @author 10040 port :9563
 */
@Component
public class Server implements CommandLineRunner {

	private static ByteBuffer readBuffer = ByteBuffer.allocate(256);

	private static ByteBuffer writeBuffer = ByteBuffer.allocate(256);

	private static String ip;

	private static int port;

	public static HashMap<String, Socket> socketList = new HashMap<>();

	private static Selector selector;

	private static ServerSocketChannel ssc;

	private static Gson gson = new Gson();

	public static void go() throws IOException {
		ip = ServerConfig.getProp().getProperty("server.ip");
		port = Integer.valueOf(ServerConfig.getProp().getProperty("server.port"));
		System.out.println("-------------------i/o服务器正在启动,ip:" + ip + ",port:" + port + "----------------");
		selector = Selector.open();

		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(ip, port)); // 绑定端口监听
		// ServerSocket

		SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
		// 等待io操作
		while (true) {

			Set selectedKeys = selector.selectedKeys(); // 会带有连接的操作类型

			Iterator it = selectedKeys.iterator();
			while (it.hasNext()) {

				SelectionKey key = (SelectionKey) it.next();
				it.remove();
				if (key.isAcceptable()) {
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);

				} else if (key.isReadable() && key.isValid()) {
					readBuffer.clear();

					SocketChannel sc = (SocketChannel) key.channel();

					try {
						sc.read(readBuffer);
					} catch (IOException e) {//这样处理断线???
						System.out.println("client closed connection " + sc.getRemoteAddress());
						key.cancel();
						sc.socket().close();
						sc.close();
						break;
					}
					// 创建一个线程池来负责 I/O 事件处理中的耗时部分
					String string = new String(readBuffer.array());

					// ProcessFactory.Consumer(gson.toJson(string));
					readBuffer.flip();

					sc.write(readBuffer);
					System.out.println("received : " + new String(readBuffer.array()));
				}
			}
		}
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		go();
	}
}
