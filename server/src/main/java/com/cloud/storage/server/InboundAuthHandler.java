package com.cloud.storage.server;

import com.cloud.storage.common.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundAuthHandler extends ChannelInboundHandlerAdapter {

    boolean isClientAuthorized;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("New client tries to connect");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(isClientAuthorized) {
            ctx.fireChannelRead(msg);
            return;

        } else {
            if (msg instanceof CommandMessage) {
                String command = ((CommandMessage) msg).getCommand();
                if (command.startsWith("/auth")) {
                    String login = command.split("\\s")[1];
                    String password = command.split("\\s")[2];
                    isClientAuthorized = true;
                    ctx.pipeline().addLast(new InboundObjectHandler());
                    System.out.println("client authorized");
                }
            } else {
                System.out.println("not_authorized_bad_data");
                ctx.write(new CommandMessage("/msg not_authorized_bad_data"));
                ctx.flush();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //ctx.flush();
        ctx.close();
    }
}
