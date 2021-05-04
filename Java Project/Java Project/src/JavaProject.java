
import java.awt.*;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BlacKramDessCPT 
{
    public static void main(String[] args) throws Exception 
    {
        Window formWindow = new Window("Teacher Schedule");
    }
}

//------------------------------------------------------------SWING----------------------------------------------------------------------------------------------------
class Window extends JFrame implements ActionListener, KeyListener
{
    //initalizing variables and objects
    String awayTeacherSTR = "";
    JLabel inputTitle = new JLabel("Enter Absent Teacher(s)");
    JTextField inText = new JTextField(10);
    JButton bQuit, bEnter, bClear, bSubmit;
    JTable output, table;
    JScrollPane scroll;
    int day = 0;
    String[][] data;
    String[] columnNames = {"Teacher Name", "Period 1","Period 2","Period 3", "Period 4"};
    DefaultTableModel model;
    String[][] onCallArray;

    // Create the object that calculates the on-calls
    Calculations calculationsObj = new Calculations();

    // Cosntructor for the window to create the frame
    Window(String title) throws FileNotFoundException
    {    
        super(title);

        //creating button and chart objects
        bQuit = new JButton("Exit");
        bEnter = new JButton("Enter");
        bClear = new JButton("Clear");
        bSubmit = new JButton("Submit");
        model = new DefaultTableModel(data, columnNames);
        output = new JTable(model);
        scroll = new JScrollPane(output);

        //setting action commands 
        bQuit.setActionCommand("quit");
        bEnter.setActionCommand("enter");
        bClear.setActionCommand("clear");
        bSubmit.setActionCommand("submit");

        //creating a layout and adding buttons to the window
        setLayout(new FlowLayout());

        add(inputTitle);
        add(inText);
        add(bEnter);
        add(bClear);
        add(bSubmit);
        add(bQuit);
        getContentPane().add(scroll);

        //adding action listeners 
        inText.addKeyListener(this);
        bQuit.addActionListener(this);
        bEnter.addActionListener(this);
        bClear.addActionListener(this);
        bSubmit.addActionListener(this);
        
        // background form to hide code
        setSize(1920, 1080);
        setVisible(true);
    }

    //method that determines what occurs based on the button pressed
    public void actionPerformed(ActionEvent e) 
    {
       if (e.getActionCommand().equals("quit"))
        {
            System.exit(0);
        } 
        if (e.getActionCommand().equals("enter"))
        {
            awayTeacherSTR = awayTeacherSTR.concat(inText.getText() + ".");
            inText.setText("");       
        }
        
        if(e.getActionCommand().equals("submit"))
        {
            //String []awayTeacher = new String[ctr];
            String[] awayTeacher = awayTeacherSTR.split("\\.");
            
            // When the submit button gets pressed, create the array that shows the on-calls and fill it
            calculationsObj.createStorageArray(awayTeacher);
            calculationsObj.calculateOnCalls(awayTeacher);

            // Get the array and print it to swing
            onCallArray = calculationsObj.getStorageArray();
            String[] item = new String[5];

            for (int rows = 0; rows < onCallArray.length; rows ++) // For each row
            {
                for (int cols = 0; cols < onCallArray[rows].length; cols++) // For each column in the row
                {
                    item[cols] = onCallArray[rows][cols];   
                }
                model.addRow(item);
            }
        }

        // empties the text field
        if (e.getActionCommand().equals("clear"))
        {
            awayTeacherSTR = "";
            inText.setText("");
            while(model.getRowCount()>0)
            {
                model.removeRow(0);
            }
            
            // accounts for a 5 day work week. Resets the weekly on call counters after 5 days
            day++;
            // Resets the pergatory queues at the end of the day.
            calculationsObj.resetPergatory();
            if (day % 5 == 0)
            {
                calculationsObj.periodOneQueue.resetWeeklyOncalls();
                calculationsObj.periodTwoQueue.resetWeeklyOncalls();
                calculationsObj.periodThreeQueue.resetWeeklyOncalls();
                calculationsObj.periodFourQueue.resetWeeklyOncalls();
                
            }
        }
    }

    //gathering keyboard input for the ENTER key
    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                awayTeacherSTR = awayTeacherSTR.concat(inText.getText() + ".");
            inText.setText("");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
//------------------------------------------------------------END OF SWING--------------------------------------

class Queue 
{
    private Node first = null;
    private Node last = null;

    class Node 
    {
        // Takes the parent of the teacher class as data
        TeacherBlueprint data;
        Node link;

        Node(TeacherBlueprint data, Node link) 
        {
            this.data = data;
            this.link = link;
        }
    }

    // Queue at the back
    void enqueue(TeacherBlueprint item) 
    {
        Node newNode = new Node(item, null);
        if (last == null)
        {
            first = last = newNode;
        }
        else
        {
            last = last.link = newNode;
        }
    }

    // Queue at the front
    void enqueueFirst(TeacherBlueprint item)
    {
        Node newNode = new Node(item, null);

        if(last == null)
        {
            first = newNode;
            last = newNode;
        }
        else
        {
            Node temp = first;
            first = newNode;
            first.link = temp;
        }
    }

    // Remove from the front of the queue
    void dequeue()
    {
        if (first != null)
        {
            first = first.link;
            if (first == null)
            {
                last = null;
            }
        }
        else
        {
            throw new RuntimeException("Queue is empty");
        }
    }

    // Prints the queue
    void printQueue() {
        for (Node temp = first; temp != null; temp = temp.link) {
            System.out.println(temp.data.name);
        }
    }

    // Checks if the teacher's name in the parameters exists in the queue
    boolean nameExists(String whatToFind)
    {
        for (Node temp = first; temp != null; temp = temp.link) {
            if (whatToFind.equals(temp.data.name))
            {
                return true;
            }
        }
        return false; 
    }

    // Returns the teacher object of the teacher's name in the parameters
    TeacherBlueprint getTeacherBlueprint(String whoToGet)
    {
        TeacherBlueprint placeHolder = null;
        for (Node temp = first; temp != null; temp = temp.link) {
            if (whoToGet.equals(temp.data.name))
            {
                placeHolder = temp.data;
            }
        }
        return placeHolder;
    }

    // Moves the teacher at the front of the queue to the back
    void moveToBack() 
    {
        Node temp = first;
        dequeue();
        enqueue(temp.data);
    }

    // Returns the teachers at the front of the queue's first name
    String getFirstQueueName()
    {
        return first.data.name;
    }

    // Updates the total supervisions of the teacher at the front of the queue
    void updateFirstQueue()
    {
        first.data.updateTotalSupervisionAssignments();
        first.data.updateCurrentWeeklyOnCalls();
        first.data.updateDaysInARow();
    }

    // Adds the teacher at the front to the designated pergatory queue and removes them from this queue
    void addToPergatory(String pergatoryName, Queue pergatory)
    {
        pergatory.enqueue(getTeacherBlueprint(pergatoryName));
        dequeue();
    }

    // Adds all the teachers from the pergatory queue to the front of the current queue. Removes all teachers from pergatory
    void removeAllFromPergatory(Queue pergatory)
    {
        for (Node temp = pergatory.first; temp != null; temp = temp.link) {
            enqueueFirst(temp.data);
            pergatory.dequeue();
        }
    }

    // Checks if the teacher designated in the parameters has an appropriate amount of supervisions
    boolean appropriateAmountOfSupervisions(String teacherName)
    {
        TeacherBlueprint checking = getTeacherBlueprint(teacherName);
        if (checking.numberOfTotalSupervisionAssignments < 20 && checking.currentWeeklyOnCallsDone < 3 && checking.daysInARow < 2)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Checks if the queue is empty
    boolean checkEmpty()
    {
        if (first == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // resets the amount of on calls performed in the week
    void resetWeeklyOncalls()
    {
        for (Node temp = first; temp != null; temp = temp.link) 
        {
            temp.data.resetCurrentWeeklyOnCalls();
        }
    }

    // resets the amount of on calls performed in a row
    void resetTwoConsecutiveOnCalls(String whatIsIt, String[][] onCallArray)
    {
        // If what is being reset is a pergatory queue
        if (whatIsIt.equals("pergatory"))
        {
            for (Node temp = first; temp != null; temp = temp.link) 
            {
                temp.data.resetTwoConsecutiveOnCalls();
            }
        }
        // If what is being reset is a period queue
        else if (whatIsIt.equals("period"))
        {
            // If the node is not in the current on-call list, rest the daily counter
            for (Node temp = first; temp != null; temp = temp.link) 
            {
                Boolean nameIsInList = false;
                for (int rows = 0; rows < onCallArray.length; rows ++) // For each row
                {
                    for (int cols = 0; cols < onCallArray[rows].length; cols++) // For each column in the row
                    {
                        String name = onCallArray[rows][cols];
                        if (temp.data.name.equals(name))
                        {
                            nameIsInList = true;
                        } 
                    }
                }
                if (nameIsInList == false)
                {
                    temp.data.resetTwoConsecutiveOnCalls();
                }
            }
        }
    }
}

// Parent class of the teachers.
abstract class TeacherBlueprint
{
    // Has a name, and a total amount of supervisions, and a method to update the total
    protected String name;
    protected boolean twoConsecutiveOnCalls; 
    protected int numberOfTotalSupervisionAssignments, currentWeeklyOnCallsDone, daysInARow; // Max is 20 total for an on-Call Teacher, 3 for a Lunch teacher.
    protected abstract void updateTotalSupervisionAssignments();  
    // these must be in this class or else they will not be recognized by the rest of the code for some reason
    protected abstract void updateCurrentWeeklyOnCalls(); 
    protected abstract void updateDaysInARow();
    protected abstract void resetCurrentWeeklyOnCalls();
    protected abstract void resetTwoConsecutiveOnCalls();    
}

// Child of the teacher, this teacher has on-calls
abstract class onCallTeacherBlueprint extends TeacherBlueprint
{
    // Has a variable for what period they have an oncall
    protected int onCallPeriod;
}

abstract class LunchTeacherBlueprint extends TeacherBlueprint
{
    // If the teacher is currently on lunch duty for the week.
    protected boolean currentSupervisionWeek;
}


class onCallTeacher extends onCallTeacherBlueprint
{
    // Constructor to define the variables in the abstract class.
    onCallTeacher(String name, int onCallPeriod)
    {
        this.name = name;
        this.onCallPeriod = onCallPeriod;
        numberOfTotalSupervisionAssignments = currentWeeklyOnCallsDone = 0;
        twoConsecutiveOnCalls = false;
        daysInARow = 0;
    }

    // Accumulator for the total on-calls
    public void updateTotalSupervisionAssignments()
    {
        numberOfTotalSupervisionAssignments ++;
    }

    // Accumulator for the weekly on-calls
    public void updateCurrentWeeklyOnCalls() 
    {
        currentWeeklyOnCallsDone ++;
    }

    // resets the amount of on calls after a week is completed
    public void resetCurrentWeeklyOnCalls()
    {
        currentWeeklyOnCallsDone = 0;
    }

    // resets after a day gap between on calls in order to get back on track
    public void resetTwoConsecutiveOnCalls()
    {
        daysInARow = 0;
    }

    // adds 1 for every day the teacher has worked in a row
    public void updateDaysInARow()
    {
        daysInARow++;
    }
}


class LunchTeacher extends LunchTeacherBlueprint
{
    // Constructor to define the variables in the abstract class.
    LunchTeacher(String name)
    {  
        this.name = name;
        currentSupervisionWeek = false;
        numberOfTotalSupervisionAssignments = 0;
    }

    // Accumulator for the total lunch duties ==> Max should be 3
    public void updateTotalSupervisionAssignments() 
    {
       numberOfTotalSupervisionAssignments ++; 
    }
    
    // these methods need to be here or else it will break, because they are methods in the teacher blueprint class. No way around
    protected void updateCurrentWeeklyOnCalls() 
    {
    }
    protected void resetCurrentWeeklyOnCalls() 
    {
    }
    protected void resetTwoConsecutiveOnCalls() 
    {
    }
    protected void updateDaysInARow()
    {
    }
}

class Calculations
{
    // Create each period's queue. Teachers with that on-call period belong to that queue.
    Queue periodOneQueue = new Queue();
    Queue periodTwoQueue = new Queue();
    Queue periodThreeQueue = new Queue();
    Queue periodFourQueue = new Queue();
    Queue lunchPeriodQueue = new Queue();

    // Create the pergatory queues for each period. These queues store teachers who are not eligible to do an on-call
    Queue pergatoryP1 = new Queue();
    Queue pergatoryP2 = new Queue();
    Queue pergatoryP3 = new Queue();
    Queue pergatoryP4 = new Queue();
    
    // Array that will store the on-calls
    String[][] storageArray;

    Calculations() throws FileNotFoundException
    {
        // Populate queues when object is created
        populateQueues();
    }
    
    void populateQueues() throws FileNotFoundException
    {
        // Get teacher names from file
        File teacherFile = new File("teachschedule.txt");
        Scanner fileScanner = new Scanner(teacherFile);

        // While there is a next teacher
        while(fileScanner.hasNextLine())
        {
            // Initalize variables used to find teacher data
            int counterPeriod = 0;
            String name = "";
            int period = 0;
            TeacherBlueprint currentTeacher = null;

            // Scanner 
            String line = fileScanner.nextLine();
            Scanner scannedLine = new Scanner(line);
            
            while(scannedLine.hasNext())
            {
                String currentToken = scannedLine.next();
                // Coutner will be 0 for the name
                if (counterPeriod == 0)
                {
                    name = currentToken;
                    // THESE IF STATEMENTS ARE A BRUTE FORCE METHOD... IF HE USES OTHER NAMES THIS WILL BREAK FOR TWO WORD LAST NAMES
                    if (name.equals("De"))
                    {
                        name = name.concat(" " + scannedLine.next());
                    }
                    else if (name.equals("Di"))
                    {
                        name = name.concat(" " + scannedLine.next());
                    }
                    counterPeriod ++;
                }
                // Else find what period the teacher has an on-call or lunch duty
                else
                {
                    // Determines where the teacher has an on-call
                    if (currentToken.equals("0"))
                    {
                        period = counterPeriod;
                        currentTeacher = new onCallTeacher(name, period);
                        break;
                    }
                    // Determines where the teacher has a lunch duty
                    else if (currentToken.equals("L"))
                    {
                        period = 99;
                        currentTeacher = new LunchTeacher(name);
                        break;
                    }
                    // Didnt find any duty yet, add to the counter and re-loop
                    else
                    {
                        counterPeriod ++;
                    }
                }
            }

            // enqueues the current teacher
            if (period == 1)
            {
                periodOneQueue.enqueue(currentTeacher);
            }
            else if (period == 2)
            {
                periodTwoQueue.enqueue(currentTeacher);
            }
            else if (period == 3)
            {
                periodThreeQueue.enqueue(currentTeacher);
            }
            else if (period == 4)
            {
                periodFourQueue.enqueue(currentTeacher);
            }
            else if (period == 99)
            {
                lunchPeriodQueue.enqueue(currentTeacher);
            }

            // Reset variables
            counterPeriod = 0;
            period = 0;
            currentTeacher = null;
            scannedLine.close();
        }
        fileScanner.close();
    }

    String[][] calculateOnCalls(String[] awayTeachers)
    {
        // Create the array that will store the data, it's rows are twice as long as each teacher needs two rows. The first 
        // contains their name and the first half of the on-call, the next contains the second half of that on-call.
        // If there is a blank, that means that the teacher has that period off.

        // Array looks like: [Teacher name] [Period 1 on-call] [Period 2 on-call] [Period 3 on-call] [Period 4 on-call] 
        //                   [Blank  Space] [Period 1 on-call] [Period 2 on-call] [Period 3 on-call] [Period 4 on-call] 

        // Pergatory arrays are used to store the teachers who are away or not eligible so they dont show up in another teachers on-call list.
        int teacherCounter = 0;
        for (String teacher : awayTeachers) 
        {
            String currentTeacher = teacher;
            boolean doTheyTeachP1 = false;
            boolean doTheyTeachP2 = false;
            boolean doTheyTeachP3 = false;
            boolean doTheyTeachP4 = false;
            boolean doTheyHaveLunchDuty = false;

            // Block of if statements that checks where the teachers name occurs in the on-call queue list. If it finds the name in that on-call list
            // set the teaching variables for every other period to true.
            // Teacher has an on-call P1
            if (periodOneQueue.nameExists(teacher) || pergatoryP1.nameExists(teacher))
            {
                doTheyTeachP2 = doTheyTeachP3 = doTheyTeachP4 = true;
            }
            // Teacher has an on-call P2
            else if (periodTwoQueue.nameExists(teacher) || pergatoryP2.nameExists(teacher))
            {
                doTheyTeachP1 = doTheyTeachP3 = doTheyTeachP4 = true;
            }
            // Teacher has an on-call P3
            else if (periodThreeQueue.nameExists(teacher) || pergatoryP3.nameExists(teacher))
            {
                doTheyTeachP1 = doTheyTeachP2 = doTheyTeachP4 = true;
            }
            // Teacher has an on-call P4
            else if (periodFourQueue.nameExists(teacher) || pergatoryP4.nameExists(teacher))
            {
                doTheyTeachP1 = doTheyTeachP2 = doTheyTeachP3 = true;
            }
            // Teacher has lunch duty
            else if (lunchPeriodQueue.nameExists(teacher))
            {
                doTheyTeachP1 = doTheyTeachP2 = doTheyTeachP4 =true;
                doTheyHaveLunchDuty = true;
            }

            // Creates the first column of the rows in the storage array. [Teacher Name]
            //                                                            [Blank  Space]
            storageArray[teacherCounter][0] = teacher;
            storageArray[teacherCounter + 1][0] = " ";

            // Get on calls for P1
            if (doTheyTeachP1)
            {

                // Variable and while loops is to check and see if the teacher who is being assigned an on-call is valid. I.e is presnt and has
                // a valid amount of total / weekly supervisions.
                boolean goodReplacement = false;
                boolean goodReplacement2 = false;
                boolean empty1 = false;
                boolean empty2 = false;
                while (!goodReplacement)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodOneQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodOneQueue.addToPergatory(periodOneQueue.getFirstQueueName(), pergatoryP1);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty1 = true;
                        }
                        if (periodOneQueue.checkEmpty()) // if there are no teachers available to on call
                        {
                            empty1 = true;
                        }
                    }
                    if (periodOneQueue.checkEmpty()) // no teachers are available to on call
                    {
                        empty1 = true;
                        goodReplacement = true;
                        break;
                    }
                    
                    if (periodOneQueue.appropriateAmountOfSupervisions(periodOneQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodOneQueue.addToPergatory(periodOneQueue.getFirstQueueName(), pergatoryP1);
                    }
                    else
                    {
                        goodReplacement = true;
                    }
                }

                // If there are no available teachers, fill a supply teacher
                if (empty1)
                {
                    storageArray[teacherCounter][1] = "Supply Teacher";
                }
                else
                {
                    // Udates the period one first slot
                    storageArray[teacherCounter][1] = periodOneQueue.getFirstQueueName();

                    // Updates the teachers weekly on-calls
                    periodOneQueue.updateFirstQueue();

                    // Move that teacher to the back of the queue
                    periodOneQueue.moveToBack();
                }
                
                // Checks if the second teacher being assigned is valid. I.e is present and has a valid amount of total / weekly supervisions
                while (!goodReplacement2)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodOneQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodOneQueue.addToPergatory(periodOneQueue.getFirstQueueName(), pergatoryP1);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty2 = true;
                        }
                        if (periodOneQueue.checkEmpty())
                        {
                            empty2 = true;
                        }
                    }
                    if (periodOneQueue.checkEmpty())
                    {
                        empty2 = true;
                        goodReplacement2 = true;
                        break;
                    }
                    
                    if (periodOneQueue.appropriateAmountOfSupervisions(periodOneQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodOneQueue.addToPergatory(periodOneQueue.getFirstQueueName(), pergatoryP1);
                    }
                    else
                    {
                        goodReplacement2 = true;
                    }
                }

                // If there are no available teachers, fill a supply teacher
                if (empty2)
                {
                    storageArray[teacherCounter + 1][1] = "Supply Teacher";
                }
                else
                {
                    // Updates the period one second slot
                    storageArray[teacherCounter + 1][1] = periodOneQueue.getFirstQueueName();
                    
                    // Updates the teachers weekly on-calls
                    periodOneQueue.updateFirstQueue();
                    
                    // Movse that teacher to the back of the queue
                    periodOneQueue.moveToBack();
                } 
            }
            else
            {
                // The teacher has an on-call this period, therefore it is blank
                storageArray[teacherCounter][1] = "";
                storageArray[teacherCounter + 1][1] = "";
            }

            // Get on calls for P2
            if (doTheyTeachP2)
            {
                // Variable and while loops is to check and see if the teacher who is being assigned an on-call is valid. I.e is presnt and has
                // a valid amount of total / weekly supervisions.
                boolean goodReplacement = false;
                boolean goodReplacement2 = false;
                boolean empty1 = false;
                boolean empty2 = false;
                while (!goodReplacement)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodTwoQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodTwoQueue.addToPergatory(periodTwoQueue.getFirstQueueName(), pergatoryP2);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty1 = true;
                        }
                        if (periodTwoQueue.checkEmpty())
                        {
                            empty1 = true;
                        }
                    }
                    if (periodTwoQueue.checkEmpty())
                    {
                        empty1 = true;
                        goodReplacement = true;
                        break;
                    }
                    
                    if (periodTwoQueue.appropriateAmountOfSupervisions(periodTwoQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodTwoQueue.addToPergatory(periodTwoQueue.getFirstQueueName(), pergatoryP2);
                    }
                    else
                    {
                        goodReplacement = true;
                    }
                }

                // If there are no available teachers, fill a supply teacher
                if (empty1)
                {
                    storageArray[teacherCounter][2] = "Supply Teacher";
                }
                else
                {
                    // Udates the period two first slot
                    storageArray[teacherCounter][2] = periodTwoQueue.getFirstQueueName();
                    // Update the total assignments and move the teacher to the back of the queue
                    periodTwoQueue.updateFirstQueue();
                    periodTwoQueue.moveToBack();
                }

                 // Checks if the second teacher being assigned is valid. I.e is present and has a valid amount of total / weekly supervisions
                while (!goodReplacement2)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodTwoQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodTwoQueue.addToPergatory(periodTwoQueue.getFirstQueueName(), pergatoryP2);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty2 = true;
                        }
                        if (periodTwoQueue.checkEmpty())
                        {
                            empty2 = true;
                        }
                    }
                    if (periodTwoQueue.checkEmpty())
                    {
                        empty2 = true;
                        goodReplacement2 = true;
                        break;
                    }

                    if (periodTwoQueue.appropriateAmountOfSupervisions(periodTwoQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodTwoQueue.addToPergatory(periodTwoQueue.getFirstQueueName(), pergatoryP2);
                    }
                    else
                    {
                        goodReplacement2 = true;
                    }                    
                }

                // If there are no available teachers, fill a supply teacher
                if (empty2)
                {
                    storageArray[teacherCounter + 1][2] = "Supply Teacher";
                }
                else
                {
                    // Updates the period two second slot
                    storageArray[teacherCounter + 1][2] = periodTwoQueue.getFirstQueueName();
                    // Update the total assignments and move the teacher to the back of the queue
                    periodTwoQueue.updateFirstQueue();
                    periodTwoQueue.moveToBack();
                }

            }
            else
            {
                // The teacher has an on-call this period, therefore it is blank
                storageArray[teacherCounter][2] = "";
                storageArray[teacherCounter + 1][2] = "";
            }

            // Get on calls for P3
            if (doTheyTeachP3)
            {
                // Variable and while loops is to check and see if the teacher who is being assigned an on-call is valid. I.e is presnt and has
                // a valid amount of total / weekly supervisions.
                boolean goodReplacement = false;
                boolean goodReplacement2 = false;
                boolean empty1 = false;
                boolean empty2 = false;
                while (!goodReplacement)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodThreeQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodThreeQueue.addToPergatory(periodThreeQueue.getFirstQueueName(), pergatoryP3);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty1 = true;
                        }
                        if (periodThreeQueue.checkEmpty())
                        {
                            empty1 = true;
                        }
                    }
                    if (periodThreeQueue.checkEmpty())
                    {
                        empty1 = true;
                        goodReplacement = true;
                        break;
                    }
                    
                    if (periodThreeQueue.appropriateAmountOfSupervisions(periodThreeQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodThreeQueue.addToPergatory(periodThreeQueue.getFirstQueueName(), pergatoryP3);
                    }
                    else
                    {
                        goodReplacement = true;
                    }
                }

                // If there are no available teachers, fill a supply teacher
                if (empty1)
                {
                    storageArray[teacherCounter][3] = "Supply Teacher";
                }
                else
                {
                    // Udates the period three first slot
                    storageArray[teacherCounter][3] = periodThreeQueue.getFirstQueueName();
                    
                    // Updates the total assignments and moves the teacher to the back of the queue
                    periodThreeQueue.updateFirstQueue();
                    periodThreeQueue.moveToBack();
                }
                // Checks if the second teacher being assigned is valid. I.e is present and has a valid amount of total / weekly supervisions
                while (!goodReplacement2)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodThreeQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodThreeQueue.addToPergatory(periodThreeQueue.getFirstQueueName(), pergatoryP3);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty2 = true;
                        }
                        if (periodThreeQueue.checkEmpty())
                        {
                            empty2 = true;
                        }
                    }
                    if (periodThreeQueue.checkEmpty())
                    {
                        empty2 = true;
                        goodReplacement2 = true;
                        break;
                    }
                    
                    if (periodThreeQueue.appropriateAmountOfSupervisions(periodThreeQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodThreeQueue.addToPergatory(periodThreeQueue.getFirstQueueName(), pergatoryP3);
                    }
                    else
                    {
                        goodReplacement2 = true;
                    }                    
                }
                
                // If there are no available teachers, fill a supply teacher
                if (empty2)
                {
                    storageArray[teacherCounter + 1][3] = "Supply Teacher";
                }
                else
                {
                    // Updates the period three second slot
                    storageArray[teacherCounter + 1][3] = periodThreeQueue.getFirstQueueName();

                    // Updates the total teachers assignments and moves them to the back of the queue
                    periodThreeQueue.updateFirstQueue();
                    periodThreeQueue.moveToBack();
                }

            }
            else
            {
                // The teacher has an on-call this period, therefore it is blank
                storageArray[teacherCounter][3] = "";
                storageArray[teacherCounter + 1][3] = "";
            }

            // Get on calls for P4
            if (doTheyTeachP4)
            {
                // Variable and while loops is to check and see if the teacher who is being assigned an on-call is valid. I.e is presnt and has
                // a valid amount of total / weekly supervisions.
                boolean goodReplacement = false;
                boolean goodReplacement2 = false;
                boolean empty1 = false;
                boolean empty2 = false;
                while (!goodReplacement)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodFourQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodFourQueue.addToPergatory(periodFourQueue.getFirstQueueName(), pergatoryP4);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty1 = true;
                        }
                        if (periodFourQueue.checkEmpty())
                        {
                            empty1 = true;
                        }
                    }
                    if (periodFourQueue.checkEmpty())
                    {
                        empty1 = true;
                        goodReplacement = true;
                        break;
                    }
                    
                    if (periodFourQueue.appropriateAmountOfSupervisions(periodFourQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodFourQueue.addToPergatory(periodFourQueue.getFirstQueueName(), pergatoryP4);
                    }
                    else
                    {
                        goodReplacement = true;
                    }
                }

                // If there are no available teachers, fill a supply teacher
                if (empty1)
                {
                    storageArray[teacherCounter][4] = "Supply Teacher";
                }
                else
                {
                    // Udates the period four first slot
                    storageArray[teacherCounter][4] = periodFourQueue.getFirstQueueName();

                    // Updates the total teaching assignments and moves them to the back of the queue
                    periodFourQueue.updateFirstQueue();
                    periodFourQueue.moveToBack();
                }

                // Checks if the second teacher being assigned is valid. I.e is present and has a valid amount of total / weekly supervisions
                while (!goodReplacement2)
                {
                    for (String teacherName : awayTeachers) 
                    {
                        try
                        {
                            // checks if they have the appropriate requirements to fulfill an on call
                            if (periodFourQueue.getFirstQueueName().equals(teacherName)) 
                            {
                                periodFourQueue.addToPergatory(periodFourQueue.getFirstQueueName(), pergatoryP4);
                            }   
                        }
                        // Error occurs if no teacher is available, therefore use a supply teacher
                        catch (NullPointerException e)
                        {
                            empty2 = true;
                        }
                        if (periodFourQueue.checkEmpty())
                        {
                            empty2 = true;
                        }
                    }
                    if (periodFourQueue.checkEmpty())
                    {
                        empty2 = true;
                        goodReplacement2 = true;
                        break;
                    }
                    
                    if (periodFourQueue.appropriateAmountOfSupervisions(periodFourQueue.getFirstQueueName()) == false) // checks if the teachers meet the requirements to fill an on call
                    {
                        periodFourQueue.addToPergatory(periodFourQueue.getFirstQueueName(), pergatoryP4);
                    }
                    else
                    {
                        goodReplacement2 = true;
                    }  
                }

                // If there are no available teachers, fill a supply teacher
                if (empty2)
                {
                    storageArray[teacherCounter + 1][4] = "Supply Teacher";
                }
                else
                {
                    // Updates the period four second slot
                    storageArray[teacherCounter + 1][4] = periodFourQueue.getFirstQueueName();

                    // Update the total assignments and moves them to the back of the queue
                    periodFourQueue.updateFirstQueue();
                    periodFourQueue.moveToBack();
                }
            }
            else
            {
                // The teacher has an on-call this period, therefore it is blank
                storageArray[teacherCounter][4] = "";
                storageArray[teacherCounter + 1][4] = "";
            }

            // The teacher counter is incremented by two as each teacher is assigned two rows.
            teacherCounter += 2;
        }
        return storageArray;
    }

    void resetPergatory()
    {
        // Removes teachers from pergatory and adds them back into the original queue.
        // Resets all consecutive on-calls for teachers didn't do an on-call.
        pergatoryP1.resetTwoConsecutiveOnCalls("pergatory", storageArray);
        periodOneQueue.resetTwoConsecutiveOnCalls("period", storageArray);
        periodOneQueue.removeAllFromPergatory(pergatoryP1);
        pergatoryP2.resetTwoConsecutiveOnCalls("pergatory", storageArray);
        periodTwoQueue.resetTwoConsecutiveOnCalls("period", storageArray);
        periodTwoQueue.removeAllFromPergatory(pergatoryP2);
        pergatoryP3.resetTwoConsecutiveOnCalls("pergatory", storageArray);
        periodThreeQueue.resetTwoConsecutiveOnCalls("period", storageArray);
        periodThreeQueue.removeAllFromPergatory(pergatoryP3);
        pergatoryP4.resetTwoConsecutiveOnCalls("pergatory", storageArray);
        periodFourQueue.resetTwoConsecutiveOnCalls("period", storageArray);
        periodFourQueue.removeAllFromPergatory(pergatoryP4);
    }

    void createStorageArray(String[] awayTeachers)
    {
         // Create the array that will store the data, it's rows are twice as long as each teacher needs two rows. The first 
        // contains their name and the first half of the on-call, the next contains the second half of that on-call.
        // If there is a blank, that means that the teacher has that period off.

        // Array looks like: [Teacher name] [Period 1 on-call] [Period 2 on-call] [Period 3 on-call] [Period 4 on-call] 
        //                   [Blank  Space] [Period 1 on-call] [Period 2 on-call] [Period 3 on-call] [Period 4 on-call] 
        storageArray = new String[awayTeachers.length * 2][5];
    }

    //accessor method to get the storage array 
    String[][] getStorageArray()
    {
        return storageArray;
    }
}