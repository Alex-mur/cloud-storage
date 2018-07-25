package com.cloud.storage.server;

import com.cloud.storage.common.CommandMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class InboundObjectHandler extends ChannelInboundHandlerAdapter {

    private String userLogin;
    private String userDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;

            if (msg instanceof CommandMessage) {
                String command = ((CommandMessage) msg).getCommand();

                if (command.equals(CommandMessage.GET_FILE_LIST)) {
                    ctx.write(new CommandMessage(CommandMessage.GET_FILE_LIST, FilesHandler.listDirectory(userLogin)));
                }
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //ctx.flush();
        ctx.close();
    }

    public InboundObjectHandler(String userLogin) {
        this.userLogin = userLogin;
        userDir = SQLHandler.getUserFolderByLogin(userLogin);
        System.out.println("InboundObjectHandler  init. User: " + userLogin + " Dir: " + userDir);
    }
}
