package com.github.wrdlbrnft.codebuilder.code;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 09/12/14
 */
class WriteableImpl implements Writeable {

    private final Writeable mWriteable;

    public WriteableImpl(final String code) {
        mWriteable = new Writeable() {
            @Override
            public void write(StringBuilder builder) {
                builder.append(code);
            }
        };
    }

    public WriteableImpl(Writeable builder) {
        mWriteable = builder;
    }

    public WriteableImpl(final CodeBlock codeBlock) {
        mWriteable = new Writeable() {
            @Override
            public void write(StringBuilder builder) {
                builder.append(codeBlock.toString());
            }
        };
    }

    @Override
    public void write(StringBuilder builder) {
        mWriteable.write(builder);
    }
}
