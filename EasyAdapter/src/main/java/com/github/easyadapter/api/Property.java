package com.github.easyadapter.api;

/**
 * {@link java.lang.Enum} which identifies to which property of the {@link android.view.View View} the
 * value is supposed to be bound. This information combined with the return type of the method or
 * the output type of the {@link com.github.easyadapter.api.Formater} will be used to make
 * assumptions about the type of the target {@link android.view.View View} (the actual type can only be
 * determined at runtime). See the individual values for a description about the assumptions made to
 * determine the type of the {@link android.view.View View}.
 */
public enum Property {

    /**
     * <p>
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Required Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>{@link java.lang.String String}</td>
     *         <td>Text for a {@link android.widget.TextView TextView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.TextView TextView}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link int} or {@link java.lang.Integer Integer}</td>
     *         <td>String Resource id identifying a text for a {@link android.widget.TextView TextView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.TextView TextView}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link boolean} or {@link java.lang.Boolean Boolean}</td>
     *         <td>Checked State of a {@link android.widget.CheckBox CheckBox}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.CheckBox CheckBox}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.graphics.drawable.Drawable Drawable}</td>
     *         <td>Image for an {@link android.widget.ImageView ImageView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.ImageView ImageView}</td>
     *     </tr>
     *     <tr>
     *         <td>Everything else</td>
     *         <td>Text (calls {@link java.lang.String#valueOf(java.lang.Object)}) for a {@link android.widget.TextView TextView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.TextView TextView}</td>
     *     </tr>
     * </table>
     * </p>
     */
    AUTO,

    /**
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Required Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>Any Object</td>
     *         <td>Text (calls {@link java.lang.String#valueOf(java.lang.Object)}) for a {@link android.widget.TextView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.TextView TextView}</td>
     *     </tr>
     * </table>
     */
    TEXT,

    /**
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Assumed Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>{@link int} or {@link java.lang.Integer}</td>
     *         <td>String resource id identifying a text for a {@link android.widget.TextView TextView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.TextView TextView}</td>
     *     </tr>
     * </table>
     */
    TEXT_RESOURCE,

    /**
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Assumed Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>{@link int} or {@link java.lang.Integer}</td>
     *         <td>Drawable resource id identifying a background {@link android.graphics.drawable.Drawable Drawable} for a {@link android.view.View View}</td>
     *         <td>Any {@link android.view.View View}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.graphics.drawable.Drawable Drawable}</td>
     *         <td>{@link android.graphics.drawable.Drawable Drawable} as background for a {@link android.view.View View}</td>
     *         <td>Any {@link android.view.View View}</td>
     *     </tr>
     * </table>
     */
    BACKGROUND,

    /**
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Assumed Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>{@link int} or {@link java.lang.Integer}</td>
     *         <td>Drawable resource id identifying an image for an {@link android.widget.ImageView ImageView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.ImageView ImageView}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link android.graphics.drawable.Drawable Drawable}</td>
     *         <td>{@link android.graphics.drawable.Drawable Drawable} as image for an {@link android.widget.ImageView ImageView}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.ImageView ImageView}</td>
     *     </tr>
     * </table>
     */
    IMAGE,

    /**
     * Interprets the type of {@link android.view.View View} and how the value should be bound according
     * to this table:
     * <table>
     *     <tr>
     *          <th>Return Type</th>
     *          <th>Assumed Type of Data</th>
     *          <th>Assumed Type of View</th>
     *     </tr>
     *     <tr>
     *         <td>{@link boolean} or {@link java.lang.Boolean}</td>
     *         <td>Checked state of a {@link android.widget.CheckBox}</td>
     *         <td>Any {@link android.view.View View} extending {@link android.widget.CheckBox CheckBox}</td>
     *     </tr>
     * </table>
     */
    CHECKED_STATE
}
