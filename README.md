EasyAdapter
===========

A powerful yet lightweight view injection & list creation library to easily and quickly create complex lists in the `RecyclerView` on Android.

Basic Usage
-----------

First create the layout for your list items. They may look something like this:

```xml
<?xml version="1.0" encoding="utf-8"?>

<android.support.v7.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:cardview="http://schemas.android.com/apk/res-auto"
    android:id="@+id/card"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    cardview:cardCornerRadius="4dp">

    <TextView
        android:id="@+id/textview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="12dp"/>

</android.support.v7.widget.CardView>
```

Then create the `ViewModel` classes like this

```java
@Layout(id = R.layout.list_item)
public class ExampleViewModel implements ViewModel {

    @BindToView(id = R.id.textview)
    private String text;

    public ExampleViewModel(String text) {
        this.text = text;
    }
}
```

Each view models has to implement the `ViewModel` interface and it has to be annotated with `@Layout` to specify the layout for this model. 

The `@BindToView` annotation specifies to which `View` the value is supposed to be bound to. In this case the String `text` will be set to the `TextView` with the id `R.id.textview`.

And that is pretty much it! You can instatiate the `EasyAdapter` like any other `Adapter` and supply it with a `List` of your view models.

```java
final List<ViewModel> models = new ArrayList<>();

for (int i = 0; i < 100; i++) {
    ExampleViewModel model = new ExampleViewModel(String.valueOf(i));
    models.add(model);
}

EasyAdapter<ViewModel> adapter = new EasyAdapter<ViewModel>(context, models);
...
recyclerView.setAdapter(adapter);
```

The result would look like this:

![First example image](http://i.imgur.com/S2MeLEH.png)

You can display all kinds of different view model classes in the same `RecyclerView`, just add them to the same `List` and pass that `List` to the `EasyAdapter`. The `EasyAdapter` is already optimized for efficient view recycling with many different kinds of view items. You can use the generic type parameter of `EasyAdapter` to narrow down which view model classes are allowed in the `Adapter`. `EasyAdapter<ViewModel>` allows any kind of view model class.

Advanced Usage
--------------

The `@BindToView` annotation is very smart about how it binds the supplied data to a `View`. For example the following code would set the `String` text as text of the `CheckBox` and the `boolean` checked as its checked state:

```java
@BindToView(id = R.id.checkbox)
private String text;
  
@BindToView(id = R.id.checkbox)
private boolean checked;
```

The same goes for resources, you can bind string resources to `TextViews`, `EditTexts` or `CheckBoxes` and drawable resources to `ImageViews` and so on:

```java
@BindToView(id = R.id.textview)
private int textResId;

@BindToView(id = R.id.imageview)
private int drawableResId;
```

You can also bind the same value to multiple `Views` like this:

```java
@BindToView(id = {R.id.textview, R.id.checkbox})
private String text;
```

The deciding factor for how the data is applied is always the type of the `View` and the type of the `Field`. The following tables shows how values of different types are interpreted for each `View` by default:

|  | `TextView` | `Checkbox` | `ImageView` |
| --- | --- | --- | --- |
| `String`| as text | as text | - |
| `int` | as text resource | as text resource | as drawable resource |
| `float` | calls `.toString()` | calls `.toString()` | - |
| `double` | calls `.toString()` | calls `.toString()` | - |
| `boolean` | calls `.toString()` | as checked state | - |
| `Drawable` | as background | as background | as image |
| `Bitmap` | as background | as background | as image |
| everything else | calls `.toString()` | calls `.toString()` | - |

Of course this is not all this library does! The `@BindToView` annotation has a second optional parameter with which you can specify how the data is supposed to be applied to the `View`. As long as you don't specify a specific way how the data is supposed to be bound then the data will be bound exactly as described in the table above. Every `View` which is not in the table above, but is a subclass of one of the `Views` above will be treated as one of those `Views`. For example an `EditText` is treated as a `TextView` in the table above and so on. All other `Views` which are not a subclass of one of the three `Views` above will not apply the data without specifying how to apply it. 

You can specify how to apply the data like this:

```java
@BindToView(id = R.id.text, property = Property.AUTO_DETECT)
```

`Property` is an enum with the following values:

| Value | Description |
| ----- | ----------- |
| `AUTO_DETECT` | This is the default value. If you specify this than the data will be applied according to the table above. |
| `TEXT` | The data will be interpreted as text. Essentially calls `.toString()` on the `Object` and sets it as text if possible |
| `IMAGE`| The data will be interpreted as an image. `Drawable`, `Bitmap` and an `int` or `Integer` drawable resource are possible values. |
| `BACKGROUND`| The data will be interpreted as background. `Drawable`, `Bitmap` and an `int` or `Integer` drawable resource are possible values. |
| `BACKGROUND_COLOR` | The data will be interpreted as background color. Only `int` or `Integer` color code values are possible (**NO** color resources!!!1). |
| `ALPHA` | The data will be interpreted as alpha value for the `View`. `int`, `Integer`, `float`, `Float`, `double` and `Double` values are possible. |
| `VISIBILITY` | The data will be interpreted as visibility value! Can be a `boolean` or `Boolean` (`true` equals `View.VISIBLE`, `false` equals `View.GONE`) or it can be `View.VISIBLE`, `View.INVISIBLE` or `View.GONE`. |
| `TRANSLATION_X` | The data will be interpreted as translation value along the x axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `TRANSLATION_Y` | The data will be interpreted as translation value along the y axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `TRANSLATION_Z` | The data will be interpreted as translation value along the z axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `ROTATION` | The data will be interpreted as rotation value around the z axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `ROTATION_X` | The data will be interpreted as rotation value around the x axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `ROTATION_Y` | The data will be interpreted as rotation value around the y axis. Can be a `int`, `Integer`, `float`, `Float`, `double` or `Double` value. |
| `CHECKED_STATE` | The data will be interpreted as checked state. Can be a `boolean` or `Boolean`. |
| `ENABLED` | The data will be interpreted as enabled state. Can be a `boolean` or `Boolean`. |

Date Formatting
--------------

You can use the `@DateFormat` annotation to define how `Date` objects are formatted. You can choose what information you want to display (date, time or both) and you can choose between a long and a short version. The information you want to display is then formatted **according to the current locale**!

```java
@DateFormat(format = DateFormat.Format.SHORT_DATE_TIME)
@BindToView(id = R.id.textview)
private Date date;
```

The code above would format a short version of both the date and time. The results would look like this for different languages:

| Language | Result |
| ------ | ------ |
| German | 22.11.14 14:29 |
| English | 11/22/14 2:29 PM | 

`@DateFormat` should be used in most cases as it correctly formats the date according to the locale, but if you want to have more control over how the `Date` is formatted then you can use the `@DatePattern` annotation to supply a specific date pattern, but then it will be formatted the same way in **every** locale. The pattern follows the rules of `SimpleDateFormat` and you can look up those rules [here](http://developer.android.com/reference/java/text/SimpleDateFormat.html).

```java
@DatePattern(pattern = "hh:mm:ss")
@BindToView(id = R.id.textview)
private Date date;
```

Number Formatting
--------------

To format numbers you can use the `@NumberFormat` annotation. You have to supply it with a pattern according to the rules of the `DecimalFormat` class. You can find those rules [here](http://developer.android.com/reference/java/text/DecimalFormat.html).

```java
@NumberFormat(pattern = "#.000")
@BindToView(id = R.id.textview)
private float number;
```

Events and Callbacks
--------------

Currently there are four supported callbacks through annotations.

| Annotation | Description |
| ---------- | ----------- |
| `@OnBind` | Methods annotated with this annotation are executed when the view model is bound to a `View` in the `RecyclerView`. | 
| `@OnUnbind` | Methods annotated with this annotation are executed when the view model is unbound from a `View` in the `RecyclerView`. |
| `@OnClick` | You have to specify a view id for this annotation. Should the specified `View` be clicked than the annotated method will be executed. |
| `@OnCheckedChanged` | You have to specify the view id of a `CheckBox` for this annotation. Should the `CheckBox` be checked or unchecked then the annotated method will be executed. |

For example to react to a click on a `View` with the id `R.id.card` you have to include this annotated method in your view model:

```java
@OnClick(id = R.id.card)
public void onClick(Context context) {
    Toast.makeText(context, "View R.id.card was clicked!", Toast.LENGTH_SHORT).show();
}
```

The parameters of annotated methods are automatically injected if possible. You can inject a `Context`, the `Adapter` instance itself, `Views` or custom objects. More on that in the next chapter.

You can also react to the checked change events of a `CheckBox` like this:

```java
@OnCheckedChanged(id = R.id.checkbox)
public void onCheckedChange() {
    Toast.makeText(context, "Checkbox R.id.checkbox was toggled!", Toast.LENGTH_SHORT).show();
}
```

To implement other listeners or some other not supported functionality you can use the `@OnBind` or `@OnUnbind` annotations. More on that in the last chapter. A example implementation of `@OnBind` and `@Unbind` would look like this:

```java
@OnBind
public void bind(Context context) {
    Toast.makeText(context, "Bound: " + text, Toast.LENGTH_SHORT).show();
}

@OnUnbind
public void unbind(Context context) {
    Toast.makeText(context, "Unbound: " + text, Toast.LENGTH_SHORT).show();
}
```

Injecting Views and custom Objects
--------------

You can inject `View` instances into fields and methods through the use of the `@InjectView` annotation. You can then access these `View` instances in the annotated callback methods mentioned in the previous chapter. For example you can inject a `View` into a field like this:

```java
@InjectView(id = R.id.textview)
private TextView textView;
```

Or you can inject `Views` into methods like this:

```java
@OnClick(id = R.id.textview)
public void onClick(Context context, @InjectView(id = R.id.textview) TextView textView) {

}
```

You can also inject a `Context` instance or the `EasyAdapter` instance itself into a field through the use of the `@Inject` annotation:

```java
@Inject
private Context context;
```

But the main use of the `@Inject` annotation is to inject custom objects into your view models. For example you can define a listener interface like this:

```java
public interface ExampleListener {
    public void notify();   
}
```

You can implement this interface however you like, for example in the `Fragment` which contains the `RecyclerView`:

```java
public class ExampleFragment implements ExampleListener {

    ...

    public void notify() {
        // Do something
    }
}
```

When you create the `EasyAdapter` instance you can provide an array of objects through the constructor which later can be injected into the view models. In this case we provide the `ExampleFragment` which now implements our `ExampleListener`:

```java
EasyAdapter<ViewModel> adapter = new EasyAdapter<ViewModel>(getActivity(), models, this, ...);
```

In the view model class we can now use `@Inject` to inject the `ExampleListener` instance into the view models:

```java
@Inject
private ExampleListener listener;
```

Now you have access to the ExampleListener object inside the annotated callback methods mentioned in the previous chapter:

```java
@Inject
private ExampleListener listener;

@OnClick(id = R.id.textview)
public void onClick(Context context, @InjectView(id = R.id.textview) TextView textView) {
    listener.notify();
}
```

Implementing custom behaviour or not supported features
---------

What is not supported directly through the use of annotations can easily be implemented through the use of `@OnBind` and `@OnUnbind`.

**The most important thing about this is the possibility to mess up the view recycling if you forget to unset listeners or something similar in the unbind method. All listeners and references which are set in the bind method have to be unset in the unbind method.**

As an example we will implement an `OnFocusChangedListener` in a view model:

```java
@OnBind
public void bind(@InjectView(id = R.id.textview) TextView textView) {
    textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // Do something
        }
    });
}

@OnUnbind
public void unbind(@InjectView(id = R.id.textview) TextView textView) {
    // NEVER forget to unset the listener again
    textView.setOnFocusChangeListener(null);
}
```
