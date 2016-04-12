AspectInj
=========

AspectInj is an **AOP** solution for **Java** via *Annotation-Processing*.This is especially useful on **Android**.

---

## What can AspectInj Do

Imagine there's a set of existing Java code, and some *aspect logic* needs to be added.The most direct way is to add static *before* and *after* methods arround each method.Obviously it's a much too intrusive solution, but **AspectInj** can help you to add these join point entries, without any changes to your method code.What you need to do is just adding an annotation to your class.

## How to Use

### 1.Add AspectInj's *jar* file

The *jar* file can be compiled by module *aspectinj*

### 2.Add *JoinPoints* annotation to the target class:  

<pre><code>@JoinPoints
public class MainActivity extends FragmentActivity {
</code></pre>

### 3.Register point cuts by implementing *PointCutRegistery*:  
<pre><code>public class DemoPointCut implements PointCutRegistery {
    @Override
    public Map<String, PointCut> register() {
        map.put("demo.windning.view.MainActivity.onCreate.class#0.onClick(View)",
        new PointCut(){
            @Override
            public boolean onBefore(Object self, Object[] args) {
            	...
                return true;
            }
        });
    }
</code></pre>  

### 4.Invoke *weave* before any main logic:   

<pre><code>AspectInjector.weave(new DemoPointCut());</code></pre> 

## Guides

Assuming there is a class *Demo* like this:

<pre><code>package com.windning.demo
//demo class
public class Demo {
    //member function
    public void onCreate(Object inst) {
        ...
        //anonymous class
        listener = new OnClickListener() {
            //anonymous class's method
            @Override
            public void onClick(View target) {

            }
        }
    }

    //inner class
    private class DemoModel{
        //inner class's method
        private void init(int arg) {

        }
    }
}
</code></pre>

### * How to add *PointCut* around some specific method

<pre><code>public class [UserOwnRegistery] implements PointCutRegistery {
    @Override
    public Map<String, PointCut> register() {
        map.put([method position], [customized point cut function]);
    }
</code></pre>

The point is to register correct *method position*(a String) and customize *point cut function*.

#### 1.*method position* of normal method:  

*onCreate* in *Demo*

<pre><code>"com.windning.demo.Demo.onCreate(Object)"</code></pre>

#### 2.*method position* of inner class's method:  

*init* in *Demo.DemoModel*

<pre><code>"com.windning.demo.Demo.DemoModel.init(int)"</code></pre>

#### 3.*method position* of anonymous class's method:  

*onClick* in *Demo.onCreate*

<pre><code>"com.windning.demo.Demo.onCreate.class#0.onClick(View)"</code></pre>   

Note that an anonymous class is identified by *"class#"* prefix and an *index*.The *index* is the order defined in current class/method block counted from 0.
