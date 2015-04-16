package com.github.easyadapter.annotations;

import com.github.easyadapter.api.Formater;
import com.github.easyadapter.api.Property;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>
 *     Defines that the return value of the annotated method needs to be bound to the property of a
 *     {@link android.view.View}. Parameters of the annotated method will be satisfied if possible,
 *     see {@link com.github.easyadapter.annotations.Inject} and
 *     {@link com.github.easyadapter.annotations.InjectView}.
 * </p>
 * <p>
 * {@link com.github.easyadapter.annotations.BindToView} has to specify one or more
 * {@link java.lang.Integer} ids which identify the {@link android.view.View Views} to which the
 * return value will be bound.
 * </p>
 * <p>
 * If no property or {@link com.github.easyadapter.api.Property#AUTO} is specified then the return
 * value will be interpreted according to the following table:
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
 *
 * The {@link com.github.easyadapter.api.Formater} is used to format or transform the return values
 * of the method into a form or type which can be bound to a {@link android.view.View}. For example
 * the {@link com.github.easyadapter.api.Formater.DateToString DateToString Formater} formats a
 * {@link java.util.Date} return value into a localized date {@link java.lang.String} which can be
 * bound to a {@link android.widget.TextView}. If no {@link com.github.easyadapter.api.Formater} or
 * the {@link com.github.easyadapter.api.Formater.Default Default Formater} is specified than the
 * return value of the method will directly be bound to the {@link android.view.View}.
 */
@Target(ElementType.METHOD)
public @interface BindToView {
    public int[] value();
    public Property property() default Property.AUTO;
    public Class<? extends Formater<?, ?>> formater() default Formater.Default.class;
}
