package com.cloud.storage.server;

import com.cloud.storage.common.CommandMessage;
import com.cloud.storage.common.FileMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.ArrayList;

public class InboundObjectHandler extends ChannelInboundHandlerAdapter {

    private String userLogin;
    private String userDir;
    private ArrayList<FileReceiver> receiveQueue;

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
                    ctx.write(new CommandMessage(CommandMessage.GET_FILE_LIST, FilesHandler.listDirectory(userDir)));
                    ctx.flush();
                }

                if (command.equals(CommandMessage.SEND_FILE_REQUEST)) {
                    if (FilesHandler.isFileExist(userDir, ((CommandMessage) msg).getFileName())) {
                        ctx.write(new CommandMessage(CommandMessage.SEND_FILE_DECLINE_EXIST, ((CommandMessage) msg).getFileName()));
                        ctx.flush();
                    } else if (((CommandMessage) msg).getFileSize() >= FilesHandler.getAvailableSize(userDir)) {
                        ctx.write(new CommandMessage(CommandMessage.SEND_FILE_DECLINE_SPACE, ((CommandMessage) msg).getFileName()));
                        ctx.flush();
                    } else {
//                        FilesHandler.createTempFile(userDir, ((CommandMessage) msg).getFileName());
                        receiveQueue.add(new FileReceiver(userDir, ((CommandMessage) msg).getFileName(), ((CommandMessage) msg).getFileSize()));
                        ctx.write(new CommandMessage(CommandMessage.SEND_FILE_CONFIRM, ((CommandMessage) msg).getFileName()));
                        ctx.write(new CommandMessage(CommandMessage.GET_FILE_LIST, FilesHandler.listDirectory(userDir)));
                        ctx.flush();
                    }
                }
            }

            if (msg instanceof FileMessage) {
                System.out.println("get file message");
                for (int i = 0; i < receiveQueue.size(); i++) {
                    if (receiveQueue.get(i).getFinalFileName().equals(((FileMessage) msg).getName())) {
                        if (receiveQueue.get(i).receiveFile((FileMessage) msg)) {
                            receiveQueue.remove(i);
                            ctx.write(new CommandMessage(CommandMessage.MESSAGE, "File received: " + ((FileMessage) msg).getName()));
                            ctx.write(new CommandMessage(CommandMessage.GET_FILE_LIST, FilesHandler.listDirectory(userDir)));
                            ctx.flush();
                        }
                        break;
                    }
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
        this.receiveQueue = new ArrayList<>();
        userDir = SQLHandler.getUserFolderByLogin(userLogin);
        System.out.println("InboundObjectHandler  init. User: " + userLogin + " Dir: " + userDir);
    }
}
