// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tcp_packet.proto

package com.github.capture.model;

public final class TcpPacketOuterClass {
  private TcpPacketOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface TcpPacketOrBuilder extends
      // @@protoc_insertion_point(interface_extends:TcpPacket)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>fixed64 ts = 1;</code>
     * @return The ts.
     */
    long getTs();

    /**
     * <code>fixed32 nanos = 2;</code>
     * @return The nanos.
     */
    int getNanos();

    /**
     * <code>int32 packet_len = 3;</code>
     * @return The packetLen.
     */
    int getPacketLen();

    /**
     * <code>bytes raw_data = 4;</code>
     * @return The rawData.
     */
    com.google.protobuf.ByteString getRawData();
  }
  /**
   * Protobuf type {@code TcpPacket}
   */
  public static final class TcpPacket extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:TcpPacket)
      TcpPacketOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use TcpPacket.newBuilder() to construct.
    private TcpPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private TcpPacket() {
      rawData_ = com.google.protobuf.ByteString.EMPTY;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new TcpPacket();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private TcpPacket(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 9: {

              ts_ = input.readFixed64();
              break;
            }
            case 21: {

              nanos_ = input.readFixed32();
              break;
            }
            case 24: {

              packetLen_ = input.readInt32();
              break;
            }
            case 34: {

              rawData_ = input.readBytes();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return TcpPacketOuterClass.internal_static_TcpPacket_descriptor;
    }

    @Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return TcpPacketOuterClass.internal_static_TcpPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              TcpPacket.class, Builder.class);
    }

    public static final int TS_FIELD_NUMBER = 1;
    private long ts_;
    /**
     * <code>fixed64 ts = 1;</code>
     * @return The ts.
     */
    @Override
    public long getTs() {
      return ts_;
    }

    public static final int NANOS_FIELD_NUMBER = 2;
    private int nanos_;
    /**
     * <code>fixed32 nanos = 2;</code>
     * @return The nanos.
     */
    @Override
    public int getNanos() {
      return nanos_;
    }

    public static final int PACKET_LEN_FIELD_NUMBER = 3;
    private int packetLen_;
    /**
     * <code>int32 packet_len = 3;</code>
     * @return The packetLen.
     */
    @Override
    public int getPacketLen() {
      return packetLen_;
    }

    public static final int RAW_DATA_FIELD_NUMBER = 4;
    private com.google.protobuf.ByteString rawData_;
    /**
     * <code>bytes raw_data = 4;</code>
     * @return The rawData.
     */
    @Override
    public com.google.protobuf.ByteString getRawData() {
      return rawData_;
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (ts_ != 0L) {
        output.writeFixed64(1, ts_);
      }
      if (nanos_ != 0) {
        output.writeFixed32(2, nanos_);
      }
      if (packetLen_ != 0) {
        output.writeInt32(3, packetLen_);
      }
      if (!rawData_.isEmpty()) {
        output.writeBytes(4, rawData_);
      }
      unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (ts_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeFixed64Size(1, ts_);
      }
      if (nanos_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeFixed32Size(2, nanos_);
      }
      if (packetLen_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, packetLen_);
      }
      if (!rawData_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, rawData_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof TcpPacket)) {
        return super.equals(obj);
      }
      TcpPacket other = (TcpPacket) obj;

      if (getTs()
          != other.getTs()) return false;
      if (getNanos()
          != other.getNanos()) return false;
      if (getPacketLen()
          != other.getPacketLen()) return false;
      if (!getRawData()
          .equals(other.getRawData())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TS_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getTs());
      hash = (37 * hash) + NANOS_FIELD_NUMBER;
      hash = (53 * hash) + getNanos();
      hash = (37 * hash) + PACKET_LEN_FIELD_NUMBER;
      hash = (53 * hash) + getPacketLen();
      hash = (37 * hash) + RAW_DATA_FIELD_NUMBER;
      hash = (53 * hash) + getRawData().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static TcpPacket parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TcpPacket parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TcpPacket parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TcpPacket parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TcpPacket parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static TcpPacket parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static TcpPacket parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static TcpPacket parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static TcpPacket parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static TcpPacket parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static TcpPacket parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static TcpPacket parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(TcpPacket prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code TcpPacket}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:TcpPacket)
        TcpPacketOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return TcpPacketOuterClass.internal_static_TcpPacket_descriptor;
      }

      @Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return TcpPacketOuterClass.internal_static_TcpPacket_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                TcpPacket.class, Builder.class);
      }

      // Construct using com.github.capture.model.TcpPacketOuterClass.TcpPacket.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @Override
      public Builder clear() {
        super.clear();
        ts_ = 0L;

        nanos_ = 0;

        packetLen_ = 0;

        rawData_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return TcpPacketOuterClass.internal_static_TcpPacket_descriptor;
      }

      @Override
      public TcpPacket getDefaultInstanceForType() {
        return TcpPacket.getDefaultInstance();
      }

      @Override
      public TcpPacket build() {
        TcpPacket result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public TcpPacket buildPartial() {
        TcpPacket result = new TcpPacket(this);
        result.ts_ = ts_;
        result.nanos_ = nanos_;
        result.packetLen_ = packetLen_;
        result.rawData_ = rawData_;
        onBuilt();
        return result;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof TcpPacket) {
          return mergeFrom((TcpPacket)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(TcpPacket other) {
        if (other == TcpPacket.getDefaultInstance()) return this;
        if (other.getTs() != 0L) {
          setTs(other.getTs());
        }
        if (other.getNanos() != 0) {
          setNanos(other.getNanos());
        }
        if (other.getPacketLen() != 0) {
          setPacketLen(other.getPacketLen());
        }
        if (other.getRawData() != com.google.protobuf.ByteString.EMPTY) {
          setRawData(other.getRawData());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        TcpPacket parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (TcpPacket) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long ts_ ;
      /**
       * <code>fixed64 ts = 1;</code>
       * @return The ts.
       */
      @Override
      public long getTs() {
        return ts_;
      }
      /**
       * <code>fixed64 ts = 1;</code>
       * @param value The ts to set.
       * @return This builder for chaining.
       */
      public Builder setTs(long value) {
        
        ts_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>fixed64 ts = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTs() {
        
        ts_ = 0L;
        onChanged();
        return this;
      }

      private int nanos_ ;
      /**
       * <code>fixed32 nanos = 2;</code>
       * @return The nanos.
       */
      @Override
      public int getNanos() {
        return nanos_;
      }
      /**
       * <code>fixed32 nanos = 2;</code>
       * @param value The nanos to set.
       * @return This builder for chaining.
       */
      public Builder setNanos(int value) {
        
        nanos_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>fixed32 nanos = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearNanos() {
        
        nanos_ = 0;
        onChanged();
        return this;
      }

      private int packetLen_ ;
      /**
       * <code>int32 packet_len = 3;</code>
       * @return The packetLen.
       */
      @Override
      public int getPacketLen() {
        return packetLen_;
      }
      /**
       * <code>int32 packet_len = 3;</code>
       * @param value The packetLen to set.
       * @return This builder for chaining.
       */
      public Builder setPacketLen(int value) {
        
        packetLen_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 packet_len = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearPacketLen() {
        
        packetLen_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString rawData_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes raw_data = 4;</code>
       * @return The rawData.
       */
      @Override
      public com.google.protobuf.ByteString getRawData() {
        return rawData_;
      }
      /**
       * <code>bytes raw_data = 4;</code>
       * @param value The rawData to set.
       * @return This builder for chaining.
       */
      public Builder setRawData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        rawData_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes raw_data = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearRawData() {
        
        rawData_ = getDefaultInstance().getRawData();
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:TcpPacket)
    }

    // @@protoc_insertion_point(class_scope:TcpPacket)
    private static final TcpPacket DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new TcpPacket();
    }

    public static TcpPacket getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<TcpPacket>
        PARSER = new com.google.protobuf.AbstractParser<TcpPacket>() {
      @Override
      public TcpPacket parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new TcpPacket(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<TcpPacket> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<TcpPacket> getParserForType() {
      return PARSER;
    }

    @Override
    public TcpPacket getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_TcpPacket_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_TcpPacket_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\020tcp_packet.proto\"L\n\tTcpPacket\022\n\n\002ts\030\001 " +
      "\001(\006\022\r\n\005nanos\030\002 \001(\007\022\022\n\npacket_len\030\003 \001(\005\022\020" +
      "\n\010raw_data\030\004 \001(\014B\032\n\030com.github.capture.m" +
      "odelb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_TcpPacket_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_TcpPacket_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_TcpPacket_descriptor,
        new String[] { "Ts", "Nanos", "PacketLen", "RawData", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
