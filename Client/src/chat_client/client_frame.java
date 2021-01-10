// NAME: MOHAMMED THAWFEEQ ISHAQ
// UTA-ID: 1001698748
/* Sources: https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams
            https://github.com/DanielRaj1610/DFSInValidation
*/      
// References: https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
package chat_client;

// importing the java classes that are needed below: 
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class client_frame extends javax.swing.JFrame 
{
    // Initializing the username, address and port numbers
    String username, address = "localhost";
    int port = 2222;
    
    // Initializing an ArrayList for the usernames.
    // https://www.tutorialspoint.com/iterate-through-arraylist-in-java
    ArrayList<String> users = new ArrayList();
    
    // A boolean value is initialized to check whether a client is currently connected to the server or not.
    Boolean isConnected = false;
    
    // Creating a socket for the server to connect, a BufferedReader for getting the input, and a PrintWriter for writing back to the program.
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;
    
    // Creating a function (ThreadStarter) to start a new thread for each individual client.
    // http://tutorials.jenkov.com/java-concurrency/creating-and-starting-threads.html
    public void ThreadStarter() 
    {
         Thread IncomingReader = new Thread(new IncomingReader());
         IncomingReader.start();
         
         Thread monitor = new Thread(new monitorincomingfiles());
         monitor.start();
         
         Thread delete = new Thread(new delete());
         delete.start();
         
    }
    
    // A function is created for adding the users to the ArrayList created in line 25.//
    // https://www.tutorialspoint.com/iterate-through-arraylist-in-java
    public void userAdd(String data) 
    {
         users.add(data);
    }
    
    // A function is created for removing the user, in case they press the disconnect button in the GUI. Once they click that button, it will display in the TextArea that the said user is not connected anymore.
    public void userRemove(String data) 
    {
         ta_chat.append(data + " is now offline.\n");
    }
    
    // Creating a string array to hold the number of users that are currently connected. 
    public void writeUsers() 
    {
        // Creating a temporary String ArrayList to hold the number of users.
         String[] tempList = new String[(users.size())];
         // Writing the names of the users to the temporary String ArrayList. 
         users.toArray(tempList);
         // for every token in the temporary array..
         for (String token:tempList) 
         {
             //users.append(token + "\n");
         }
    }
    
    // Function for sending the disconnected message to the client. 
    public void sendDisconnect() 
    {
        // Initializes a string to hold the "user disconnected" message
        String bye = (username + ": :Disconnect");
        try
        {
            // Uses the printwriter to print it on to the textarea and flushes the printwriter. 
            writer.println(bye); 
            writer.flush(); 
        } 
        // used to catch any exceptions that may arise. 
        catch (Exception e) 
        {
            ta_chat.append("Could not send Disconnect message.\n");
        }
    }

    // Function for disconnecting the client from the server.
    public void Disconnect() 
    {
        try 
        {
            // Appending the message "Disconnected" to the TextArea and closing the socket.
            ta_chat.append("Disconnected.\n");
            sock.close();
        } 
        // used to catch any exceptions that may arise.
        catch(Exception ex) {
            ta_chat.append("Failed to disconnect. \n");
        }
        // Setting the boolean that checks whether a client is connected or not to false. 
        isConnected = false;
        // The username TextField is made editable for the different users to make changes. 
        tf_username.setEditable(true);

    }
    
    // Function that holds the GUI components
    public client_frame() 
    {
        // Calls the function that holds the GUI components.
        initComponents();
    }
    
    
    // This class uses tokens to recognize what kind of message it is.
    public class IncomingReader implements Runnable
    {
        @Override
        public void run() 
        {
            // Initializing a string array to hold the data, and a list of string variables for use in the tokens below. 
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

            try 
            {
                // Checking to see if the stream isn't null..
                while ((stream = reader.readLine()) != null) 
                {
                    // Since tokens are used, we are using colon (:) as the separator. 
                     data = stream.split(":");
                     
                     // Used for printing the contents to the TextArea (similar to a normal chat application).
                     if (data[2].equals(chat)) 
                     {
                        // 
                        ta_chat.append(data[0] + ": " + data[1] + "\n");
                        ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                     } 
                     // The client starts the connection process
                     else if (data[2].equals(connect))
                     {
                        // removes from the TextArea all of its elements that are contained in the specified collection.
                        ta_chat.removeAll();
                        // Adds the name of the user. 
                        userAdd(data[0]);
                     } 
                     // The client starts the disconnection process. 
                     else if (data[2].equals(disconnect)) 
                     {
                         // The username is removed. 
                         userRemove(data[0]);
                     } 
                     // When the server has completed its operation
                     else if (data[2].equals(done)) 
                     {
                        //users.setText("");
                        writeUsers();
                        users.clear();
                     }
                     // When a file is deleted, the voting function starts
                     else if (data[2].equals("please vote"))
                     {
                         // The user who initiated the deletion should not be allowed to vote
                         if(!data[0].equals(username)){
                            // Initializing an ArrayList that contains the phrases ABORT and COMMIT.
                            List<String> list = new ArrayList<>(); 
                            list.add("ABORT"); 
                            list.add("COMMIT"); 
                            // A randomizer is introduced that will oscillate between ABORT and COMMIT. 
                            // https://www.tutorialspoint.com/generate-a-random-array-of-integers-in-java
                            Random rand = new Random(); 
                            // Creating a new string to get a choice from the Arraylist.
                            String a = list.get(rand.nextInt(list.size()));
                            // Writing each client's response to the TextArea.
                            ta_chat.append("My response is "+ a);
                            // Using the printwriter to push out the vote of each client and then flushing it. 
                            writer.println(username + ":" + a + ":" + "myvote");
                            writer.flush();
                         }
                    }
                }
           }
           // used to catch any exceptions that may arise.
           catch(Exception ex) { 
           }
        }
    }
    
        // This class is mainly used for monitoring the directory for any files that are created or modified.
        public class monitorincomingfiles implements Runnable
        {
            @Override
            public void run()
            {
                JOptionPane.showMessageDialog(null,"Monitoring started!");
                // Setting up the Watcher Service
                // https://javacodedepot.com/tutorial/java-watchservice-example
                WatchService watchService = null;
                try {
                    watchService = FileSystems.getDefault().newWatchService();
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
 
                // The path to be monitored is given below
                Path path = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+username+"/sharedfiles");
 
                try {
                    // The path above is registered for the creation and modification notifications. 
                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
              
                // https://javacodedepot.com/tutorial/java-watchservice-example
                WatchKey key;
                try {
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            // Prints the username that made the change. 
                            writer.println(username + ":" + event.context() + ":" + "newFile/modifiyFile");
                            // Flushes the buffer
                            writer.flush(); 
                        }
                        // Resetting the Watcher service's key.
                        key.reset();
                    }       
                } 
                // used to catch any exceptions that may arise.
                catch (InterruptedException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    
        // This class is mainly used for monitoring the directory for any files that are deleted. 
        public class delete implements Runnable
        {
            @Override
            public void run()
            {
                JOptionPane.showMessageDialog(null,"Monitoring started!");
                // https://javacodedepot.com/tutorial/java-watchservice-example
                WatchService watchService = null;
                try {
                    watchService = FileSystems.getDefault().newWatchService();
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // The path to be monitored for deletions is given below. 
                Path path = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+username+"/sharedfiles");
 
                try {
                    // The path above is registered for the deletion notification.
                    path.register(watchService, StandardWatchEventKinds.ENTRY_DELETE);
                } catch (IOException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
              
 
                WatchKey key;
                try {
                    // https://javacodedepot.com/tutorial/java-watchservice-example
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            // Prints the username that made the change. 
                            writer.println(username + ":" + event.context() + ":" + "deletedFile");
                            // Flushes the buffer.
                            writer.flush(); 
                        }
                        // Resetting the Watcher service's key.
                        key.reset();
                    }       
                } 
                // used to catch any exceptions that may arise.
                catch (InterruptedException ex) {
                    Logger.getLogger(client_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lb_address = new javax.swing.JLabel();
        tf_address = new javax.swing.JTextField();
        lb_port = new javax.swing.JLabel();
        tf_port = new javax.swing.JTextField();
        lb_username = new javax.swing.JLabel();
        tf_username = new javax.swing.JTextField();
        lb_password = new javax.swing.JLabel();
        tf_password = new javax.swing.JTextField();
        b_connect = new javax.swing.JButton();
        b_disconnect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        tf_chat = new javax.swing.JTextField();
        b_send = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Client's frame");
        setName("client"); // NOI18N
        setResizable(false);

        lb_address.setText("Address : ");

        tf_address.setText("localhost");
        tf_address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_addressActionPerformed(evt);
            }
        });

        lb_port.setText("Port :");

        tf_port.setText("2222");
        tf_port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_portActionPerformed(evt);
            }
        });

        lb_username.setText("Username :");

        tf_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_usernameActionPerformed(evt);
            }
        });

        lb_password.setText("Password : ");

        b_connect.setText("Connect");
        b_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_connectActionPerformed(evt);
            }
        });

        b_disconnect.setText("Disconnect");
        b_disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_disconnectActionPerformed(evt);
            }
        });

        ta_chat.setColumns(20);
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_send.setText("Send Message");
        b_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_sendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tf_chat, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_address)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_address, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lb_port)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_port, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(b_connect))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_username, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(b_disconnect)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tf_username, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lb_password)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_password)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_address)
                    .addComponent(tf_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_port)
                    .addComponent(tf_port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lb_username)
                    .addComponent(tf_username)
                    .addComponent(lb_password)
                    .addComponent(tf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_connect)
                    .addComponent(b_disconnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_chat)
                    .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tf_addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_addressActionPerformed
       
    }//GEN-LAST:event_tf_addressActionPerformed

    private void tf_portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_portActionPerformed
   
    }//GEN-LAST:event_tf_portActionPerformed

    private void tf_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_usernameActionPerformed
    
    }//GEN-LAST:event_tf_usernameActionPerformed

    // When the "Connect" button is pressed..
    private void b_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_connectActionPerformed
        // If the client is not connected
        if (isConnected == false) 
        {
            // Get the username from the TextField
            username = tf_username.getText(); // tf_username.setEditable(false);
           
            // Create a new folder path for the user
            File f = new File("/Users/mohammedthawfeeq/Desktop/client/"+username);
            
            //  If the folder does not exist..
            if (!(f.exists() && f.isDirectory())) {                    
            try 
            {
                // Establish a new Socket with the address and port number initialized above. 
                sock = new Socket(address, port);
                
                // Setting up an InputStreamReader to receive data from the socket established above. The information is sent to the server and then the server will reply if the user is connected. 
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                // Once the connection is made, the printwriter is flushed off and the boolean isConnected is set to True 
                writer.flush(); 
                isConnected = true; 
            } 
            // used to catch any exceptions that may arise.
            catch (Exception ex) 
            {
                ta_chat.append("Cannot Connect! Try Again. \n");
                tf_username.setEditable(true);
            }
            
            // Start a new thread
            ThreadStarter();
            }
            // If the clientname is already used, an error throws up..
            else {
                JOptionPane.showMessageDialog(null,"Client name already used! Please try with another name!");
            }
        } 
        // This else if is for checking if the client is already connected. 
        else if (isConnected == true) 
        {
            ta_chat.append("You are already connected. \n");
        }
    }//GEN-LAST:event_b_connectActionPerformed

    // When the "Disconnect" button is pressed..
    private void b_disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_disconnectActionPerformed
        sendDisconnect();
        Disconnect();
    }//GEN-LAST:event_b_disconnectActionPerformed

    // When the "Send Message" button is pressed..
    private void b_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_sendActionPerformed
        
        // A null string is initialized
        String nothing = "";
        // Getting the string from the TextField and if it is equal to the null string above, do the following:
        if ((tf_chat.getText()).equals(nothing)) {
            tf_chat.setText("");
            /* requestFocus() makes a request that the given Component gets set to a focused state. 
            This method requires that the component is displayable, focusable and visible */
            tf_chat.requestFocus();
        } else {
            try {
                // Print the name of the user along with the chat content and print the word chat to denote that a specific user wants to chat. 
               writer.println(username + ":" + tf_chat.getText() + ":" + "Chat");
               writer.flush(); // flushes the buffer
            } 
            catch (Exception ex) {
                ta_chat.append("Message was not sent. \n");
            }
            tf_chat.setText("");
            tf_chat.requestFocus();
        }

        tf_chat.setText("");
        tf_chat.requestFocus();
    }//GEN-LAST:event_b_sendActionPerformed

    // The main function
    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                // Setting the GUI of the client to be visible. 
                new client_frame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_connect;
    private javax.swing.JButton b_disconnect;
    private javax.swing.JButton b_send;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb_address;
    private javax.swing.JLabel lb_password;
    private javax.swing.JLabel lb_port;
    private javax.swing.JLabel lb_username;
    private javax.swing.JTextArea ta_chat;
    private javax.swing.JTextField tf_address;
    private javax.swing.JTextField tf_chat;
    private javax.swing.JTextField tf_password;
    private javax.swing.JTextField tf_port;
    private javax.swing.JTextField tf_username;
    // End of variables declaration//GEN-END:variables
}
