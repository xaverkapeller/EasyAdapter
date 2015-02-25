# EasyAdapter

A powerful yet lightweight view injection &amp; list creation library to easily and quickly create complex lists in the `RecyclerView` on Android.
* **No slow reflection, full performance**: EasyAdapter uses compile time annotation processing to generate your view holder implementations for you. No performance hit due to reflection at runtime.
* **Most errors caught at compile time**: EasyAdapter will check for most common errors at compile time and give you useful and detailed error messages.
* **Generates real debuggable code**: You can view the view holder and view holder factory implementations at any time and debug any error and behaviour. No more guessing what went wrong.

# Table of Contents

* [Basic Usage](#basic-usage)
* [Installation](#installation)
* [Proguard](#proguard)
* [Advanced Usage](#advanced-usage)
  * [Binding Values to Views](#binding-values-to-views)
  * [Injecting Views](#injecting-views)
  * [Injecting Listeners or custom Objects](#injecting-listeners-or-custom-objects)
  * [Reacting to Bind and Unbind Events](#reacting-to-bind-and-unbind-events)
  * [Reacting to Click Events](#reacting-to-click-events)
  * [Reacting to Checked Changed Events](#reacting-to-checked-changed-events)
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
    private final int drawableRes;

    public ExampleModel(String text, int drawableRes) {
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

`@Layout` is used to define the layout associated with this model and `@BindToView` defines how to bind the data to the views. All view models also need to implement the `ViewModel` interface.

And that is all you have to do! The rest is handled by the library. You can then just create a `List` of your models and pass them into the `EasyAdapter`.

```java
final List<ViewModel> models = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    models.add(new ExampleModel(String.valueOf(i), R.drawable.some_background));
}

EasyAdapter<ViewModel> adapter = new EasyAdapter<>(getActivity(), models);
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

## Injecting Views

You can inject one or multiple `View` instances into any of your model methods by annotating a parameter with the `@InjectView` annotation like this:

```java
@OnBind
public void bind(@InjectView(R.id.someLayout) LinearLayout someLayout) {
    ...
}
```

## Injecting Listeners or custom Objects

You can inject any kind of listener or object instance into any method in your model class by annotating the parameters of the method with the `@Inject` annotation. But note that you have to provide these objects to the `EasyAdapter` instance at runtime. For example you can create a listener interface like this:

```java
public interface ExampleListener {
    public void onClick(ExampleModel model);
}
```

After implementing the interface you can provide the instance to the `EasyAdapter` in its constructor:

```java 
EasyAdapter<ViewModel> adapter = new EasyAdapter<>(getActivity(), models, exampleListenerImplementation);
```

And after that any method in your model class which has an `ExampleListener` as parameter will be provided with the `ExampleListener` instance you passed into the `EasyAdapter`:

```java
@OnClick(R.id.someView)
public void onClick(@Inject ExampleListener listener) {
    listener.onClick(this);
}
```

By default you can inject the `Context` instance passed to the `EasyAdapter` or the `EasyAdapter` instance itself.

```java
@OnBind
public void bind(@Inject Context context, @Inject EasyAdapter adapter) {
    ...
}
```

## Custom Bind/Unbind Behaviour

Any method annotated with `@OnBind` will be called each time the model is bound to a `View`. You can use this to implement custom behaviour or features which are not covered by the `@BindToView` annotation. 

Like `@OnBind` any method annotated with `@OnUnbind` will be called each time the model is being unbound from a `View`. The main purpose of this is to unregister listeners which have been set in methods annotated with `@OnBind`. 
You can have as many methods annotated with `@OnBind` or `@OnUnbind` as you want. You can also use `@Inject` or `@InjectView` to statisfy parameters of those methods.

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

Any method annotated with `@OnClick` will be called when the referenced `View` is clicked. You can again use `@Inject` or `@InjectView` to statisfy the parameters of the method:

```java
@OnClick(R.id.someView)
public void clickedSomeView(@InjectView(R.id.someView) FrameLayout someView) {
    ...
}
```

## Reacting to Checked Changed Events

Any method annotated with `@OnCheckedChanged` will be called when the checked state of the referenced `CheckBox` is changed. You can again use `@Inject` or `@InjectView` to statisfy the parameters of the method:

```java
@OnCheckedChanged(R.id.checkBox)
public void checkedChanged(@InjectView(R.id.checkBox) CheckBox checkBox) {
    ...
}
```

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
public final class ExampleModelOne$$ViewHolder extends com.github.easyadapter.impl.AbsViewHolder<ExampleModelOne> {
  private final android.widget.TextView _a;
  private final android.view.View _b;
  private final ExampleListener _c;
  public ExampleModelOne$$ViewHolder(android.view.View a, ExampleListener b) {
    super(a);
        _c = b;
        _b = itemView.findViewById(2131230785);
        _a = (android.widget.TextView) itemView.findViewById(2131230786);
        ;
  }
  protected void performUnbind(final ExampleModelOne a) {
    _b.setOnClickListener(null);
        ;
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
        ;
  }
}
```

And a factory class like this:

```java
public final class ExampleModelOne$$ViewHolderFactory extends com.github.easyadapter.impl.AbsViewHolderFactory<ExampleModelOne> {
  private final android.view.LayoutInflater _a;
  private final ExampleListener _b;
  public ExampleModelOne$$ViewHolderFactory(android.view.LayoutInflater a, com.github.easyadapter.EasyAdapter.Injector b) {
    super(a, b);
        _a = a;
        _b = b.get(com.github.easyadapter.app.models.ExampleListener.class);
        ;
  }
  public com.github.easyadapter.impl.AbsViewHolder<ExampleModelOne> newInstance(android.view.ViewGroup a) {
    final android.view.View b = _a.inflate(2130903065, a, false);
        return new com.github.easyadapter.app.models.ExampleModelOne$$ViewHolder(b, _b);
  }
}
```

At runtime the `EasyAdapter` is looking for the factory class associated with each model and then uses the factory class to create instances of the viewholder class!

If you want to know more about how this library works feel free to study the source code yourself!
