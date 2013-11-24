import net.moraleboost.streamscraper.ScrapeException;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import java.util.ArrayList;
import java.util.TimerTask;


import java.io.IOException;
import java.net.URISyntaxException;

public class Bot extends PircBot {

    private String nickname, server, channel, streamAddress, sourceAddress;
    private String currentlyPlaying;

    public Bot(String nick, String serv, String chan, String stream, String src){
        nickname = nick;
        server = serv;
        channel = chan;
        streamAddress = stream;
        sourceAddress = src;
    }

    public void connectBot() throws IrcException, IOException {
        this.setName(nickname);
        this.connect(server);
        this.joinChannel(channel);
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message){
        if (message.equalsIgnoreCase("!song")){
            StreamParser stream = new StreamParser(streamAddress);
            sendMessage(channel, "Currently Playing:" + stream.getSongTitle());
        }

        if (message.equalsIgnoreCase("!listeners")){
            StreamParser stream = new StreamParser(streamAddress);
            sendMessage(channel, stream.getListenerCount() + "");
        }

        if (message.equalsIgnoreCase("!peaklisteners")){
            StreamParser stream = new StreamParser(streamAddress);
            sendMessage(channel, stream.getPeakListenerCount() + "");
        }

        if (message.equalsIgnoreCase("!maxlisteners")){
            StreamParser stream = new StreamParser(streamAddress);
            sendMessage(channel, stream.getMaxListenerCount() + "");
        }

        if (message.equalsIgnoreCase("!source")){
            sendMessage(channel, sourceAddress);
        }

        if (message.contains("!kick") && isAuthentic(sender)){
            String[] command = message.split(" ");
            String userToKick = command[1];
            kick(channel, userToKick);
        }

        if (message.contains("!deop") && isAuthentic(sender)){
            String[] command = message.split(" ");
            String userToDeOp = command[1];
            kick(channel, userToDeOp);
        }

        if (message.contains("!op") && isAuthentic(sender)){
            String[] command = message.split(" ");
            String userToOp = command[1];
            kick(channel, userToOp);
        }

    }

    public void updateCurrentSong() throws URISyntaxException, ScrapeException {
        StreamParser stream = new StreamParser(streamAddress);
        if (!currentlyPlaying.equals(stream.getSongTitle())){
            currentlyPlaying = stream.getSongTitle();
            sendMessage(channel, "Currently playing: " + currentlyPlaying);
        }
    }

    private boolean isAuthentic(String nick){
        User[] users = getUsers(channel);
        for (User user: users){
            if (user.getNick().equals(nick)){
                if (user.isOp()){
                    return true;
                }
            }
        }
        return false;
    }

}
