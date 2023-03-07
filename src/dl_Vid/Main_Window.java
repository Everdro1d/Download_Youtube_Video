/* Gregory Rohrschach, March, 2023
 * V1.7
 * YouTube Video Downloader
 *
 * Uses: https://github.com/yt-dlp/yt-dlp
 * 		 https://ffmpeg.org/
 */

//TODO let yt-dlp check for updates

package dl_Vid;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;

import java.awt.Font;
import java.awt.Toolkit;
import java.net.URISyntaxException;
import java.util.Objects;

public class Main_Window {

    private final JFrame frame;
    private final JTextField textField_URL;
    private String url;
    private static Main_Window window;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            System.err.println("Could not set look and feel of application.");
        }
        EventQueue.invokeLater(() -> {
            try {
                window = new Main_Window();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Main_Window() {

        frame = new JFrame();
        frame.setBounds(100, 100, 450, 200);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Download Video");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(25, 11, 383, 23);
        frame.getContentPane().add(lblTitle);

        JLabel lblURL = new JLabel("URL: ");
        lblURL.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblURL.setBounds(25, 46, 34, 17);
        frame.getContentPane().add(lblURL);

        JLabel lblSave = new JLabel("Save As");
        lblSave.setHorizontalAlignment(SwingConstants.CENTER);
        lblSave.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblSave.setBounds(175, 87, 87, 23);
        frame.getContentPane().add(lblSave);

        textField_URL = new JTextField();
        textField_URL.setBounds(69, 45, 339, 23);
        frame.getContentPane().add(textField_URL);
        textField_URL.setColumns(10);

        JButton btnVideo = new JButton("Video");
        btnVideo.setBounds(50, 108, 127, 46);
        btnVideo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        frame.getContentPane().add(btnVideo);
        btnVideo.addActionListener((e) -> {
            getURL();
            setPathVideo(url);
        });

        JButton btnAudio = new JButton("Audio");
        btnAudio.setBounds(262, 108, 127, 46);
        btnAudio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        frame.getContentPane().add(btnAudio);
        btnAudio.addActionListener((e) -> {
            getURL();
            setPathAudio(url);
        });
    }

    private void setPathVideo(String url) {
        //String s;
        String s = " -o %(title)s.%(ext)s --restrict-filenames -f \"best[ext=mp4]\" ";
        //String s2 = " -o %(title)s.%(ext)s --restrict-filenames -f \"best[ext=webm]\" ";
        download(url, s);
    }
    private void setPathAudio(String url) {
        String s = " -o %(title)s.%(ext)s --restrict-filenames -f \"bestaudio[ext=m4a]\" ";
        download(url, s);
    }
    private void getURL() {
        url = textField_URL.getText();
    }

    public void download(String url, String args) {
        String command;
        try {
            command = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(".")).toURI().getPath() + "yt-dlp.exe";
        } catch(URISyntaxException e) {
            return;
        }
        command = command.substring(1);
        command = "\""+command+"\"";
        command = command + args + url;
        final String cmd = command;
        try {
            dialog_Working d1 = popupWorking();
            d1.setVisible(true);
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    System.out.println("\""+cmd+"\"");
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();
                    System.out.println(p.exitValue());
                    d1.dispose();
                    JOptionPane.showConfirmDialog(null, "Done", "Finished!", JOptionPane.DEFAULT_OPTION);
                    audioConvert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private dialog_Working popupWorking() {
        return new dialog_Working();
    }

    private void audioConvert() {
        //https://stackoverflow.com/questions/7807015/how-to-pass-parameters-to-batch-file-using-java-textbox
        String command;
        try {
            String path = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(".")).toURI().getPath();
            String pathRead;
            pathRead =  path;
            command = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(".")).toURI().getPath() + "convert.bat " +pathRead+" "+ null +"";
        } catch(URISyntaxException e) {
            return;
        }
        final String cmd = command;
        try {
            new Thread(()->{
                Process p;
                try {
                    p = Runtime.getRuntime().exec(cmd);
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    p.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}