package com.cloud.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.SocketChannel;

public class Client {

    EventLoopGroup group = new NioEventLoopGroup();
try {
Bootstrap b = new Bootstrap();
  b.group(group)
.channel(NioSocketChannel.class)
.option(ChannelOption.TCP_NODELAY, true)
.handler(new ChannelInitializer<SocketChannel>() {
@Override
public void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline p = ch.pipeline();
                                    if (sslCtx != null) {
                                           p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                                      }
                                   //p.addLast(new LoggingHandler(LogLevel.INFO));
                                    p.addLast(new EchoClientHandler());
                             }
              });

                  // Start the client.
                    ChannelFuture f = b.connect(HOST, PORT).sync();

                  // Wait until the connection is closed.
                 f.channel().closeFuture().sync();
             } finally {
                 // Shut down the event loop to terminate all threads.
                  group.shutdownGracefully();
                }
     }
}
