# EasyAdapter

A powerful yet lightweight view injection &amp; list creation library to easily and quickly create complex lists in the RecyclerView on Android.

Instead of slow reflection `EasyAdapter` uses compile time annotation processing so there will be no performance hit at runtime. For most errors you will get appropriate error messages while compiling your app instead of at runtime.

# How to use it

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

# Advanced Usage

Under construction...

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
