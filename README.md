# EasyAdapter

A powerful yet lightweight view injection &amp; list creation library to easily and quickly create complex lists in the `RecyclerView` on Android.
* **No slow reflection, full performance**: EasyAdapter uses compile time annotation processing to generate performant and efficient view holder implementations for you. No performance hit due to reflection at runtime.
* **Most errors caught at compile time**: EasyAdapter will check for most common errors at compile time and give you useful and detailed error messages.
* **Generates real debuggable code**: You can view the view holder and view holder factory implementations at any time and debug any error and behaviour. No more guessing what went wrong.
* **Intuitive and easy to use API**: Everything works just like you expect it. Any conceivable way to use the annotations is possible and the generated implementations are optimized for your specific use case. Should their be some flaw in your logic you will get errors and warnings at compile time. 

# Table of Contents

* [Basic Usage](#basic-usage)
* [Installation](#installation)
* [Proguard](#proguard)
* [Advanced Usage](#advanced-usage)
  * [Binding Values to Views](#binding-values-to-views)
  * [Injecting Views](#injecting-views)
  * [Injecting Dependencies and reacting to Events](#injecting-dependencies-and-reacting-to-events)
  * [Using AbsViewHolder to your advantage](#using-absviewholder-to-your-advantage)
  * [Reacting to Bind and Unbind Events](#reacting-to-bind-and-unbind-events)
  * [Reacting to Click Events](#reacting-to-click-events)
  * [Reacting to Checked Changed Events](#reacting-to-checked-changed-events)
* [Planned Features](#planned-features)
* [How it Works](#how-it-works)

# Basic Usage

First create the layout file for the items in your `RecyclerView`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:id="@+id/layout"
              android:orientation="vertical"
              android:layout_width="match_parent"
              android:layout_height="wrap_content">

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

</LinearLayout>
```

Then create your model classes like usual and annoate them like this:

```java
@Layout(R.layout.list_item)
public class ExampleModel implements ViewModel {

    private final String text;
    private final int drawableResId;

    public ExampleModel(String text, int drawableResId) {
        this.text = text;
        this.drawableResId = drawableResId;
    }

    @BindToView(R.id.textView)
    public String getText() {
        return text;
    }

    @BindToView(value = R.id.layout, property = Property.BACKGROUND)
    public int getBackground() {
        return drawableResId;
    }
}
```

`@Layout` is used to define the layout associated with this model and `@BindToView` defines how to bind the data to the views. All view models also need to implement the `ViewModel` interface.

You can easily react to clicks or other events by annotating an interface like this:

```java
@Listener(ExampleModel.class)
public interface ExampleListener {

    @OnClick(R.id.layout)
    public void onClick(@Inject AbsViewHolder<ExampleModel> viewHolder);
}
```

The `@Listener` annotation defines the view model on which you want to listen for events. You can then use annotations like `@OnClick` to indicate which methods should be called on the listener for which event. Reacting to events like this is explained in more detail in the chapter [Injecting Dependencies and reacting to Events](#injecting-dependencies-and-reacting-to-events) and in subsequent chapters.

After that displaying your models is as easy as creating a `List` of them and supplying them to your `EasyAdapter` instance:

```java
final List<ExampleModel> models = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    models.add(new ExampleModel(String.valueOf(i), R.drawable.some_background));
}

EasyAdapter<ExampleModel> adapter = new EasyAdapter<>(getActivity(), models);
```

You can look at the example application contained in this repository for a more detailed demonstration of how to use this library!

# Installation

1) Just download this library and add the two modules EasyAdapter and EasyAdapterCompiler to your Android project.

2) The top of the build.gradle file of your app needs to look like this:

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.+'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'android-apt'
...
```

3) In the dependencies add these two lines at the bottom:

```
apt project(':EasyAdapterCompiler')
compile project(':EasyAdapter')
```

And that is it! Now just sync your gradle files and begin annotating your models.

# Proguard

If you use proguard then you need to add these rules to your proguard file:

```
-keep class **$$ViewHolder { *; }
-keep class **$$ViewHolderFactory { *; }
-keep class com.github.easyadapter.annotations.** { *; }
-keep @com.github.easyadapter.annotations.Layout public class * { *; }
```

# Advanced Usage

## Binding Values to Views

It is impossible for the annotation processor to know the type of your views at compile time. If you don't specify what kind of data the method returns by setting the `property` value of the `@BindToView` annotation the annotation processor has to make certain assumptions about the type of your views based on the return type of your getters. 

| Return Type  | Assumed Type of Data | Assumed View Type |
| ------------- | ------------- |  ------------- |
| `String` | Text | `TextView` |
| `int` or `Integer` | String Resource ID | `TextView`  |
| `boolean` or `Boolean` | Checked State | `CheckBox`  |
| `Drawable` | Image | `ImageView` |
| Everything else | Text (calls `.toString()`) | `TextView` |

By specifing the `property` value of the `@BindToView` annotation you can tell `EasyAdapter` what kind of data your method returns. The following table lists all possible `property` values and the required view types for those values.

| `property` value | Assumed Type of Data | Required View Type | Possible Return Types |
| ---------------- | -------------------- | ------------------ | --------------------- |
| `AUTO` | See the above Table | See the above Table | See the above Table |
| `TEXT` | Text | Any `View` extending `TextView` | Any `Object` (calls `.toString()`) |
| `TEXT_RESOURCE` | String Resource ID | Any `View` extending `TextView` | `int` or `Integer` string resource ids |
| `BACKGROUND` | Background of the `View` | Any `View` | `Drawable`, `int` or `Integer` drawable resource ids |
| `IMAGE` | Image in an `ImageView` | Any `View` extending `ImageView` | `Drawable`, `int` or `Integer` drawable resource ids |
| `CHECKED_STATE` | Checked State of a `CheckBox` | Any `View` extending `CheckBox` | `boolean` or `Boolean` |

Here are some examples:

```java

// Sets the returned string resource id as text to the TextView with id R.id.textView
@BindToView(R.id.textView)
public int getText() {
    return textResId;
}

// Calls toString() on the returned object an sets it as text to the TextView with id R.id.textView2
@BindToView(R.id.textView2)
public CustomObject getCustomObject() {
    return someObject;
}

// Sets the returned drawable as background of the View with id R.id.layout
@BindToView(value = R.id.layout, property = Property.BACKGROUND)
public Drawable getBackground() {
    return backgroundDrawable;
}

// Sets the returned boolean as checked state of the CheckBox with id R.id.checkBox
@BindToView(R.id.checkBox)
public boolean getCheckedState() {
    return checked;
}

// Sets the returned drawable resource id as image of the ImageView with id R.id.imageView
@BindToView(value = R.id.imageView, property = Property.IMAGE)
public int getImage() {
    return drawableResId;
}
```

## Injecting Views

You can inject one or multiple `View` instances into any of the methods of your model or listener interface by annotating a parameter with the `@InjectView` annotation like this:

```java
@OnBind
public void bind(@InjectView(R.id.someLayout) LinearLayout someLayout) {
    ...
}
```

## Injecting Dependencies and reacting to Events

You can inject any kind of object instance into any method in your model class or in a listener interface by annotating the parameters of the method with the `@Inject` annotation. But note that you have to provide most these objects to the `EasyAdapter` instance at runtime. If you have a controller interface which you want to use to handle the selected state of items in your list, for example:

```java
public interface SelectionController {
    public void onSelect(Item item);
    public boolean isSelected(Item item);
}
```

Then you can inject the implementation of that interface at runtime by using the `inject()` method of the `EasyAdapter`:

```java
adapter.inject(selectionControllerImplementation);
```

Note that you can only inject objects as long as the `EasyAdapter` is not attached to any `RecyclerView`. If methods in your model have a paramter of type `SelectionController` then the implementation you provided through `inject()` will be used to satisfy those parameters.

---

The same thing goes for interfaces annotated with `@Listener`. For example consider this interface:

```java
@Listener(ExampleModel.class)
public interface ExampleListener {

    @OnClick(R.id.layout)
    public void onClick(@Inject AbsViewHolder<ExampleModel> viewHolder);
}
```

After implementing the interface you can provide the instance to the `EasyAdapter` by using the `inject()` method:

```java 
EasyAdapter<ViewModel> adapter = new EasyAdapter<>(getActivity(), models);
adapter.inject(exampleListenerImplementation);
```

You can also annotate methods of your model with `@OnClick` or similar annotations. For example you can have a method like this:

```java
@OnClick(R.id.someView)
public void onClick(@InjectView(R.id.textView) TextView textView) {
    textView.setText(String.valueOf(counter++));
}
```

By default you can inject the `Context` instance passed to the `EasyAdapter` or the `EasyAdapter` instance itself without having to explicitly provide them with `inject()`. The same goes for the current `ViewModel` or `AbsViewHolder` instance although you would mostly want to do that in an interface annotated with `@Listener`. 

Injecting `Context` and the `EasyAdapter` instance:

```java
@OnBind
public void bind(@Inject Context context, @Inject EasyAdapter adapter) {
    ...
}
```

## Using AbsViewHolder to your advantage

Injecting the `AbsViewHolder` instance is very useful since it provides you an easy way to access both the current model associated with the view holder and the `View` which the model is currently bound to. There are two public fields:

 - `itemView`: This field exposes the `View` associated with the `AbsViewHolder`.
 - `currentModel`: This field exposes the model which is currently bound to the `AbsViewHolder` and in extension to the `itemView` as well. 

You can use both fields to easily create animations and/or greatly simplify listener implementations. Consider an interface like this:

```java
@Listener(ExampleModel.class)
public interface ExampleListener {

    @OnClick(R.id.layout)
    public void onClick(@Inject AbsViewHolder<ExampleModel> viewHolder);
}
```

After you implement this interface in the `Fragment` which contains your `RecyclerView` and properly inject the `Fragment` instance into the `EasyAdapter` instance with `inject()` you can use the `AbsViewHolder` instance you get from the event to perform animations and/or modify the model:

```java
public class ExampleFragment extends Fragment implements ExampleListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ...
        
        EasyAdapter<ExampleModel> adapter = new EasyAdapter(getActivity(), models);
        adapter.inject(this);
        
    }

    ...

    @Override
    public void onClick(AbsViewHolder<ExampleModel> viewHolder) {
        final ExampleModel model = viewHolder.currentModel;
        model.doSomething();
        
        final View view = viewHolder.itemView;
        view.animate().alpha(0.0f).setInterpolator(new CycleInterpolator(1.0f));
    }
}
```

## Reacting to Bind and Unbind Events

Any method annotated with `@OnBind` will be called each time the model is bound to a `View`. You can use this to implement custom behaviour or features which are not covered by the `@BindToView` annotation. 

Like `@OnBind` any method annotated with `@OnUnbind` will be called each time the model is being unbound from a `View`. The main purpose of this is to unregister listeners which have been set in methods annotated with `@OnBind`. 
You can have as many methods annotated with `@OnBind` or `@OnUnbind` as you want. You can also use `@Inject` or `@InjectView` to satisfy parameters of those methods.

If you want you can skip using `@BindToView` and completely implement any bind/unbind logic in methods annotated with `@OnBind` and `@OnUnbind`.

```java
@OnBind
public void bind(@Inject Context, @InjectView(R.id.textView) TextView textView) {
    Toast.makeText(context, "Bound model " + index, Toast.LENGTH_SHORT).show();
    textView.setText(someText);
}

@OnUnbind
public void unbind(@Inject Context) {
    Toast.makeText(context, "Unbound model " + index, Toast.LENGTH_SHORT).show();
}
```

## Reacting to Click Events

Any method annotated with `@OnClick` will be called when the referenced `View` is clicked. You can again use `@Inject` or `@InjectView` to satisfy the parameters of the method:

```java
@OnClick(R.id.someView)
public void clickedSomeView(@InjectView(R.id.someView) FrameLayout someView);
```

## Reacting to Checked Changed Events

Any method annotated with `@OnCheckedChanged` will be called when the checked state of the referenced `CheckBox` is changed. You can again use `@Inject` or `@InjectView` to statisfy the parameters of the method:

```java
@OnCheckedChanged(R.id.checkBox)
public void checkedChanged(@InjectView(R.id.checkBox) CheckBox checkBox);
```

You can use `@BindToView` in conjunction with `@OnCheckedChanged` to keep track of the checked state of a `CheckBox` in your model:

```java
@BindToView(R.id.checkBox)
public boolean getCheckedState() {
    return checkedState;
}

@OnCheckedChanged(R.id.checkBox)
public void checkedChanged(@InjectView(R.id.checkBox) CheckBox checkBox) {
    checkedState = checkBox.isChecked();
}
```

# Planned Features

- [x] Better API for listener interfaces
- [x] Extended inject functionality
- [ ] Support for more Views and data types
- [ ] More intelligent automatic binding.
- [ ] Providing access to underlying view holder for better animation support

# How it works

The EasyAdapterCompiler project contains an annotation processor. When you compile your app it is looking for classes annotated with the `@Layout` annotation and for each class that is found it creates an appropriate view holder and view holder factory class.

For example for a model like this:

```java
@Layout(R.layout.list_item_one)
public class ExampleModelOne implements ViewModel {

    private final String text;
    private final int drawableRes;

    public ExampleModelOne(String text, int drawableRes) {
        this.text = text;
        this.drawableRes = drawableRes;
    }

    @BindToView(R.id.textView)
    public String getText() {
        return text;
    }

    @BindToView(value = R.id.layout, property = Property.BACKGROUND)
    public int getBackground() {
        return drawableRes;
    }

    @OnClick(R.id.layout)
    public void onClick(@Inject ExampleListener listener) {
        listener.onClick(this);
    }
}
```

The annotation processor then creates a view holder which looks something like this:

```java
public final class ExampleModelOne$$ViewHolder extends AbsViewHolder<ExampleModelOne> {
    private final TextView _a;
    private final ExampleListener _c;
    private final View _b;

    public ExampleModelOne$$ViewHolder(View a, ExampleListener b) {
        super(a);
        _c = b;
        _b = itemView.findViewById(2131230785);
        _a = (TextView) itemView.findViewById(2131230786);
    }

    protected void performUnbind(final ExampleModelOne a) {
        _b.setOnClickListener(null);
    }

    protected void performBind(final ExampleModelOne a) {
        _a.setText(a.getText());
        _b.setBackgroundResource(a.getBackground());
        _b.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                a.onClick(_c);
            }
        });
    }
}
```

And a factory class like this:

```java
public final class ExampleModelOne$$ViewHolderFactory extends AbsViewHolderFactory<ExampleModelOne> {
    private final LayoutInflater _a;
    private final ExampleListener _b;

    public ExampleModelOne$$ViewHolderFactory(LayoutInflater a, Injector b) {
        super(a, b);
        _a = a;
        _b = b.get(ExampleListener.class);
        ;
    }

    public AbsViewHolder<ExampleModelOne> newInstance(ViewGroup a) {
        final View b = _a.inflate(2130903065, a, false);
        return new ExampleModelOne$$ViewHolder(b, _b);
    }
}
```

At runtime the `EasyAdapter` is looking for the factory class associated with each model and then uses the factory class to create instances of the viewholder class!

If you want to know more about how this library works feel free to study the source code yourself!
