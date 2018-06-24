package org.dave.bonsaitrees.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.dave.bonsaitrees.render.TESRBonsaiPot;

public class MessageReloadTreesHandler implements IMessageHandler<MessageReloadTrees, MessageReloadTrees> {
    @Override
    public MessageReloadTrees onMessage(MessageReloadTrees message, MessageContext ctx) {
        TESRBonsaiPot.clearGlLists();
        return null;
    }
}
