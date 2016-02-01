package com.undyingideas.thor.skafottet.support.firebase;

import android.content.Context;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.Constant;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class
 */
public class Utils {
    /**
     * Format the timestamp with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
    private static final Pattern EMAIL_DECODE = Pattern.compile(",", Pattern.LITERAL);
    private static final Pattern EMAIL_ENCODE = Pattern.compile(".", Pattern.LITERAL);
    private Context mContext;


    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(final Context con) {
        mContext = con;
    }

//    /**
//     * Return true if currentUserEmail equals to shoppingList.owner()
//     * Return false otherwise
//     */
//    public static boolean checkIfOwner(ShoppingList shoppingList, String currentUserEmail) {
//        return (shoppingList.getOwner() != null &&
//                shoppingList.getOwner().equals(currentUserEmail));
//    }

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(final String userEmail) {
        return EMAIL_ENCODE.matcher(userEmail).replaceAll(",");
    }
    
    /**
     * Email is being decoded just once to display real email in AutocompleteFriendAdapter
     *
//     * @see com.udacity.firebase.shoppinglistplusplus.ui.sharing.AutocompleteFriendAdapter
     */
    public static String decodeEmail(final String userEmail) {
        return EMAIL_DECODE.matcher(userEmail).replaceAll(".");
    }

    /**
     * Adds values to a pre-existing HashMap for updating a property for all of the ShoppingList copies.
     * The HashMap can then be used with {@link Firebase#updateChildren(Map)} to update the property
     * for all ShoppingList copies.
     *
     * @param sharedWith            The list of users the shopping list that has been updated is shared with.
     * @param listId           The id of the shopping list.
     * @param owner            The owner of the shopping list.
     * @param mapToUpdate      The map containing the key, value pairs which will be used
     *                         to update the Firebase database. This MUST be a Hashmap of key
     *                         value pairs who's urls are absolute (i.e. from the root node)
     * @param propertyToUpdate The property to update
     * @param valueToUpdate    The value to update
     * @return The updated HashMap with the new value inserted in all lists
     */
    public static HashMap<String, Object> updateMapForAllWithValue
    (final HashMap<String, PlayerDTO> sharedWith, final String listId,
     final String owner, final HashMap<String, Object> mapToUpdate,
     final String propertyToUpdate, final Object valueToUpdate) {

        mapToUpdate.put("/" + Constant.FIREBASE_LOCATION_USER_LISTS + "/" + owner + "/" + listId + "/" + propertyToUpdate, valueToUpdate);
        if (sharedWith != null) {
            for (final PlayerDTO playerDTO : sharedWith.values()) {
                mapToUpdate.put("/" + Constant.FIREBASE_LOCATION_USER_LISTS + "/" + playerDTO.getEmail() + "/" + listId + "/" + propertyToUpdate, valueToUpdate);
            }
        }

        return mapToUpdate;
    }

    /**
     * Adds values to a pre-existing HashMap for updating all Last Changed Timestamps for all of
     * the ShoppingList copies. This method uses {@link #updateMapForAllWithValue} to update the
     * last changed timestamp for all ShoppingList copies.
     *
     * @param sharedWith           The list of users the shopping list that has been updated is shared with.
     * @param listId               The id of the shopping list.
     * @param owner                The owner of the shopping list.
     * @param mapToAddDateToUpdate The map containing the key, value pairs which will be used
     *                             to update the Firebase database. This MUST be a Hashmap of key
     *                             value pairs who's urls are absolute (i.e. from the root node)
     * @return
     */
    public static HashMap<String, Object> updateMapWithTimestampLastChanged
    (final HashMap<String, PlayerDTO> sharedWith, final String listId,
     final String owner, final HashMap<String, Object> mapToAddDateToUpdate) {
        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap
         */
        final HashMap<String, Object> timestampNowHash = new HashMap<>();
        timestampNowHash.put(Constant.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        updateMapForAllWithValue(sharedWith, listId, owner, mapToAddDateToUpdate, Constant.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, timestampNowHash);

        return mapToAddDateToUpdate;
    }
//    /**
//     * Once an update is made to a ShoppingList, this method is responsible for updating the
//     * reversed timestamp to be equal to the negation of the current timestamp. This comes after
//     * the updateMapWithTimestampChanged because ServerValue.TIMESTAMP must be resolved to a long
//     * value.
//     *
//     * @param firebaseError      The Firebase error, if there was one, from the original update. This
//     *                           method should only run if the shopping list's timestamp last changed
//     *                           was successfully updated.
//     * @param logTagFromActivity The log tag from the activity calling this method
//     * @param listId             The updated shopping list push ID
//     * @param sharedWith         The list of users that this updated shopping list is shared with
//     * @param owner              The owner of the updated shopping list
//     */
//    public static void updateTimestampReversed(final FirebaseError firebaseError, final String logTagFromActivity,
//                                               final String listId, final HashMap<String, User> sharedWith,
//                                               final String owner) {
//        if (firebaseError != null) {
//            Log.d(logTagFromActivity, "Error updating timestamp: " + firebaseError.getMessage());
//        } else {
//            final Firebase firebaseRef = new Firebase(Constant.FIREBASE_URL);
//            firebaseRef.child(Constant.FIREBASE_LOCATION_USER_LISTS).child(owner)
//                    .child(listId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    ShoppingList list = dataSnapshot.getValue(ShoppingList.class);
//                    if (list != null) {
//                        long timeReverse = -(list.getTimestampLastChangedLong());
//                        String timeReverseLocation = Constant.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE
//                                + "/" + Constant.FIREBASE_PROPERTY_TIMESTAMP;
//
//                        /**
//                         * Create map and fill it in with deep path multi write operations list
//                         */
//                        HashMap<String, Object> updatedShoppingListData = new HashMap<String, Object>();
//
//                        updateMapForAllWithValue(sharedWith, listId, owner, updatedShoppingListData,
//                                timeReverseLocation, timeReverse);
//                        firebaseRef.updateChildren(updatedShoppingListData);
//                    }
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//                    Log.d(logTagFromActivity, "Error updating data: " + firebaseError.getMessage());
//                }
//            });
//        }
//    }
}
