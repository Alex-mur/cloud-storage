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

                if(command.equals(CommandMessage.AUTH_REQUEST)) {
                    if(checkAuth(((CommandMessage) msg).getLogin(), ((CommandMessage) msg).getPassword())) {
                        isClientAuthorized = true;
                        ctx.pipeline().addLast(new InboundObjectHandler());
                        ctx.write(new CommandMessage(CommandMessage.AUTH_CONFIRM));
                        System.out.println("client authorized with: " + ((CommandMessage) msg).getLogin() + " " + ((CommandMessage) msg).getPassword());
                    } else {
                        System.out.println("bad login password");
                        ctx.write(new CommandMessage(CommandMessage.AUTH_CONFIRM));
                    }
                } else {
                    System.out.println("message not a request");
                    ctx.flush();
                }
            } else {
                System.out.println("object not a message");
                ctx.flush();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private boolean checkAuth(String login, String password) {
        return login.equals("user") && password.equals("123");
    }
}
