// NAME: MOHAMMED THAWFEEQ ISHAQ
// UTA-ID: 1001698748
/* Source: https://drive.google.com/drive/u/0/folders/0B4fPeBZJ1d19WkR3blE4ZVNTams 
           https://github.com/DanielRaj1610/DFSInValidation
*/
/* References:  https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
                http://tutorials.jenkov.com/java-nio/path.html
                https://www.tutorialspoint.com/how-to-create-a-new-directory-by-using-file-object-in-java
                https://stackoverflow.com/questions/15571496/how-to-check-if-a-folder-exists
*/
package chat_server;

// importing the java classes that are needed below: 
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class server_frame extends javax.swing.JFrame 
{
   // Initializing the ArrayList to hold the clientoutputs and the list of users. A set of three strings are initialized for use later.
   ArrayList clientOutputStreams;
   ArrayList<String> users;
   String filename;
   String deletedbyuser;
   String finaldecision = "COMMIT";
   int counter = 0;
   
   public class ClientHandler implements Runnable	
   {
       // Setting up the bufferedreader, socket and the printwriter.
       BufferedReader reader;
       Socket s;
       PrintWriter client;

       // https://stackoverflow.com/questions/46024225/java-client-server-chat
       // https://cs.dartmouth.edu/~cbk/classes/10/12fall/notes/7/ChatServer.java
       public ClientHandler(Socket clientSocket, PrintWriter user) 
       {
            client = user;
            try 
            {
                // Assigning the socket created above to the socket in the argument, getting the data through InputStreamReader and reading using the BufferedReader. 
                s = clientSocket;
                InputStreamReader isReader = new InputStreamReader(s.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch (Exception ex) 
            {
                ta_chat.append("Unexpected error... \n");
            }

       }

       @Override
       public void run() 
       {
            // https://github.com/SaptakS/chatGUI/blob/master/MultiChatClient/Chat_Server/src/chat_server/server_frame.java
            // Initializing a string array to hold the data, and a list of string variables for use in the tokens below.
            String[] data;
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat" ;
            try 
            {
                // Checking to see if the stream isn't null..
                while ((message = reader.readLine()) != null) 
                {
                    // When the token data is received, we display it to the TextArea.
                    ta_chat.append("Received: " + message + "\n");
                    // Since tokens are used, we are using colon (:) as the separator.
                    data = message.split(":");
                    
                    // When there is a token, do the following: 
                    for (String token:data) 
                    {
                        // Display the token onto the TextArea.
                        ta_chat.append(token + "\n");
                    }

                    // The client starts the connection process
                    if (data[2].equals(connect)) 
                    {
                        // Go to the tellEveryone function and add the user to the list of connected users. 
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } 
                    
                    // The client starts the disconnection process.
                    else if (data[2].equals(disconnect)) 
                    {
                        // Go to the tellEveryone function and remove the user from the list of connected users.
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } 
                    
                    // When the chat token is being sent..
                    else if (data[2].equals(chat)) 
                    {
                        // Go to the tellEveryone function.
                        tellEveryone(message);
                    } 
                    
                    // When the delete token is being sent
                    else if (data[2].equals("deletedFile")) {
                        // Setting the filename to be the data[1] token and deletedbyuser variable to the username.
                        filename = data[1];
                        deletedbyuser = data[0];
                        // Go to the tellEveryone function. 
                        tellEveryone(data[0] + ":deleted file"+filename+ ":please vote");
                        
                    }
                    
                    // If the token is equal to that of voting..
                    else if (data[2].equals("myvote")) {
                        
                        // Increment the counter for every user that votes.
                        counter++;
                        
                        // If the token data is abort, change the final decision to abort.  
                        if(data[1].equals("ABORT"))
                        {
                            finaldecision = "ABORT";
                        }
                        
                        // If counter is equal to the number of users - 1 (since the user who initiated the deletion cannot vote),
                        if(counter==users.size()-1)
                        {
                            // And if the final decision is commit,
                            if(finaldecision.equals("COMMIT"))
                            {
                                // https://www.tutorialspoint.com/iterate-through-arraylist-in-java
                                // Iterate through the list of users.
                                Iterator i = users.iterator();
                                
                                // Assuming there is another user next in line,
                                while (i.hasNext()) {
                                
                                // https://www.geeksforgeeks.org/delete-file-using-java/
                                // The i.next() refers to the name of the user, and it sets up the file for deletion.
                                File file = new File("/Users/mohammedthawfeeq/Desktop/client/"+i.next()+"/receivedfiles/"+filename);
                                file.delete();
                                }
                            }
                         // When the counter is not equal to the number of users - 1
                         else {
                                // Set the finaldecision String to commit.
                                finaldecision="COMMIT";
                                // https://howtodoinjava.com/java/io/4-ways-to-copy-files-in-java/
                                // Copy the file from the source to the destination. 
                                Path source = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+data[0]+"/incomingfiles/"+filename);
                                Path destination = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+deletedbyuser+"/receivedfiles/"+filename);
                                
                                // The replace existing tag will replace any file that is already present and will not keep it separate.  
                                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                            }
                            
                            // Setting the counter back to 0, and the string variables to empty. 
                            counter=0;
                            filename="";
                            deletedbyuser="";
                            
                        }
                    
                    }
                    
                    // When the token is for a new file or for a modified file. 
                    else if (data[2].equals("newFile/modifiyFile")) {
                        
                      //  File copied = new File("/Users/mohammedthawfeeq/Desktop/client/"+data[0]+"/sharedfiles/"+data[1]);
                      // Indicating the source path.
                      Path source = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+data[0]+"/sharedfiles/"+data[1]);
                      // Iterating through the list of users. 
                      Iterator i = users.iterator();
                      // As long as the iterator has a next user..
                      while (i.hasNext()) {
                          // Setting up the destination path for the file to be copied from source to destination and displaying that to the TextArea. 
                          Path destination = Paths.get("/Users/mohammedthawfeeq/Desktop/client/"+i.next()+"/receivedfiles/"+data[1]);
                          Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);    
                          ta_chat.append("file transferred to all clients\n");
                      } 
                    }
                    // When none of the tokens were matched
                    else 
                    {
                        ta_chat.append("No Conditions were met. \n");
                    }
                } 
             } 
             // used to catch any exceptions that may arise.
             catch (Exception ex) 
             {
                 // Prints to the TextArea that it has lost a connection. 
                ta_chat.append("Lost a connection. \n");
                ex.printStackTrace();
                // The client name is removed from output stream. 
                clientOutputStreams.remove(client);
             } 
	} 
    }
    // Function that holds the GUI components
    public server_frame() 
    {
        // Calls the function that holds the GUI components.
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        b_start = new javax.swing.JButton();
        b_end = new javax.swing.JButton();
        b_users = new javax.swing.JButton();
        b_clear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Server's frame");
        setName("server"); // NOI18N
        setResizable(false);

        ta_chat.setColumns(20);
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_start.setText("Start Server");
        b_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_startActionPerformed(evt);
            }
        });

        b_end.setText("Stop Server");
        b_end.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_endActionPerformed(evt);
            }
        });

        b_users.setText("Online Users");
        b_users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_usersActionPerformed(evt);
            }
        });

        b_clear.setText("Clear Screen");
        b_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_clearActionPerformed(evt);
            }
        });

        jLabel1.setText("Server");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(b_end, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(b_start, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(b_users, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(b_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(243, 243, 243)
                .addComponent(jLabel1)
                .addContainerGap(266, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_start)
                    .addComponent(b_users))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_clear)
                    .addComponent(b_end))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // When the "Stop Server" button is pressed, the following function gets executed: 
    private void b_endActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_endActionPerformed
        try 
        {
            // When the end server button is pressed. 
            Thread.sleep(5000);                 //5000 milliseconds is five second.
        } 
        
        // used to catch any exceptions that may arise.
        catch(InterruptedException ex) {
            // Tries to capture any interruptions that could occur to the current thread. 
            Thread.currentThread().interrupt();
        }
        
        // 
        tellEveryone("Server:is stopping and all users will be disconnected.\n:Chat");
        ta_chat.append("Server stopping... \n");
        
        ta_chat.setText("");
    }//GEN-LAST:event_b_endActionPerformed

    private void b_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_startActionPerformed
        // When the start server button is pressed, the thread starter class is initialized. 
        Thread starter = new Thread(new ServerStart());
        starter.start();
        
        ta_chat.append("Server started...\n");
    }//GEN-LAST:event_b_startActionPerformed
    // When the "Online Users" button is pressed, the following function gets executed:
    private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_usersActionPerformed
        // Displays the list of online users. 
        ta_chat.append("\n Online users : \n");
        // For every current user that is connected, do the following:
        for (String current_user : users)
        {
            // Print the name of the current user on to the TextArea and go to the next line.
            ta_chat.append(current_user);
            ta_chat.append("\n");
        }    
        
    }//GEN-LAST:event_b_usersActionPerformed
    // When the "Clear" button is pressed, the following function gets executed:
    private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_clearActionPerformed
        // Clears the TextArea. 
        ta_chat.setText("");
    }//GEN-LAST:event_b_clearActionPerformed

    // The main function
    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() {
                // Setting the GUI of the client to be visible.
                new server_frame().setVisible(true);
            }
        });
    }
    
    // Class used for starting the server. 
    public class ServerStart implements Runnable 
    {
        @Override
        public void run() 
        {
            // Setting the client output and users a new ArrayList each. 
            clientOutputStreams = new ArrayList();
            users = new ArrayList();  

            try 
            {
                // Assigning a port number for the server socket. 
                ServerSocket serverSock = new ServerSocket(2222);
                while (true) 
                {
                                // Accepting the client socket connection. 
				Socket clientSock = serverSock.accept();
                                
                                // Setting up the PrintWriter to send the data to the client. 
				PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
				clientOutputStreams.add(writer);

                                // Creating a new thread for each client that connects to the server. 
				Thread listener = new Thread(new ClientHandler(clientSock, writer));
				listener.start();
				ta_chat.append("Got a connection. \n");
                }
            }
            // used to catch any exceptions that may arise.
            catch (Exception ex)
            {
                ta_chat.append("Error making a connection. \n");
            }
        }
    }
    
    // Class for adding the users. 
    public void userAdd (String data) 
    {
        // Assigning a string to a token, and adding the username.
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        ta_chat.append("Before " + name + " added. \n");
        users.add(name);
        ta_chat.append("After " + name + " added. \n");
        
        // Creating a temporary String array to hold the number of users. 
        String[] tempList = new String[(users.size())];
        
        // Returns an array containing all the elements in tempList in the correct order.
        users.toArray(tempList);
        
        // Setting up a home directory for the client.
        File homeDir = new File("/Users/mohammedthawfeeq/Desktop/client/"+name);
        
        // Creating two separate directories for the user to place the files for sharing and the files that are received. 
        File receivedfiles = new File("/Users/mohammedthawfeeq/Desktop/client/"+name+"/receivedfiles");
        File sharedfiles = new File("/Users/mohammedthawfeeq/Desktop/client/"+name+"/sharedfiles");
        
        // If the home directory does not exist, do the following: 
        if(!homeDir.mkdir()) {
            System.out.println("/Users/mohammedthawfeeq/Desktop/client/"+name);
        }
        
        // Creating those directories described above. 
        receivedfiles.mkdir();
        sharedfiles.mkdir();
        
        // For every token in the temporary array 
        for (String token:tempList) 
        {
            // The message will be the token + the connect operation as described below. 
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }
    
    // Class used for removing the user. 
    public void userRemove (String data) 
    {
        // Initializing a string to hold the message, and a list of string variables for use in the tokens below.
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        
        // Remove the user from the ArrayList.
        users.remove(name);
        
        // Creating a new temporary array list for storing the size of the users. 
        String[] tempList = new String[(users.size())];
        
        // Returns an array containing all the elements in tempList in the correct order.
        users.toArray(tempList);

        // For every token in the temporary array
        for (String token:tempList) 
        {
            // The message will be the token + the connect operation.
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }
    
    // This class sends a message to all the clients. 
    public void tellEveryone(String message) 
    {
        // https://www.tutorialspoint.com/iterate-through-arraylist-in-java
        // Setting up an iterator to go through the cientoutputstream. 
	Iterator it = clientOutputStreams.iterator();

        // As long as it has a next item..
        while (it.hasNext()) 
        {
            try 
            {
                // Setting up the PrintWriter to be the next value and printing the message onto the TextArea, and flushing the PrintWriter. 
                PrintWriter writer = (PrintWriter) it.next();
		writer.println(message);
		ta_chat.append("Sending: " + message + "\n");
                writer.flush();
                
                // https://www.programcreek.com/java-api-examples/?class=javax.swing.JTextArea&method=setCaretPosition
                /* Notification of changes to the caret position and the selection are sent to implementations of the CaretListener interface 
                 that have been registered with the text component. */
                ta_chat.setCaretPosition(ta_chat.getDocument().getLength());

            } 
            // used to catch any exceptions that may arise.
            catch (Exception ex) 
            {
		ta_chat.append("Error telling everyone. \n");
            }
        } 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_clear;
    private javax.swing.JButton b_end;
    private javax.swing.JButton b_start;
    private javax.swing.JButton b_users;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea ta_chat;
    // End of variables declaration//GEN-END:variables
}
