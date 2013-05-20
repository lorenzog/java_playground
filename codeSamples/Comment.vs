intMessageID = Cint(request("msg"))

'Make sure the messageID is within the proper range.
  If (intMessageID > 0) AND (intMessageID < 8) Then
                               
'Get text for messages from DB.
msgSQL = "SELECT MessageText FROM Messages WHERE MessageID BETWEEN 1 AND 7"
SET rsMsg = conn.execute(msgSQL)
               
'Convert the recordset to an array.
   arrMsg = rsMsg.GetRows()

'Display the appropriate message text.
   response.write(arrMsg(0, intMessageID - 1))
                               
End If
