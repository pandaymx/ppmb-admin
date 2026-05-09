import React, { useState, useRef, useEffect } from 'react';
import { Card, Input, Button, List, Typography, Space, message } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined } from '@ant-design/icons';
import { useAuthStore } from '../../store/useAuthStore';

const { Text } = Typography;

interface Message {
  role: 'user' | 'assistant';
  content: string;
}

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const token = useAuthStore((state) => state.token);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (!inputValue.trim()) return;

    const userMessage = inputValue.trim();
    setInputValue('');
    setMessages((prev) => [...prev, { role: 'user', content: userMessage }]);
    setLoading(true);

    try {
      const response = await fetch(`/api/ai/chat/stream?message=${encodeURIComponent(userMessage)}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const reader = response.body?.getReader();
      const decoder = new TextDecoder('utf-8');

      setMessages((prev) => [...prev, { role: 'assistant', content: '' }]);

      if (reader) {
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });

          setMessages((prev) => {
            const newMessages = [...prev];
            const lastMessage = newMessages[newMessages.length - 1];
            if (lastMessage && lastMessage.role === 'assistant') {
              lastMessage.content += chunk;
            }
            return newMessages;
          });
        }
      }
    } catch (error) {
      message.error('Failed to communicate with AI');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="AI Assistant" className="h-full flex flex-col" bodyStyle={{ display: 'flex', flexDirection: 'column', height: '600px', padding: '16px' }}>
      <div className="flex-1 overflow-y-auto mb-4 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
        <List
          dataSource={messages}
          renderItem={(msg) => (
            <List.Item className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} border-b-0`}>
              <Space align="start" className={`max-w-[80%] ${msg.role === 'user' ? 'flex-row-reverse' : ''}`}>
                <div className={`p-2 rounded-full ${msg.role === 'user' ? 'bg-blue-100 text-blue-600' : 'bg-green-100 text-green-600'}`}>
                  {msg.role === 'user' ? <UserOutlined /> : <RobotOutlined />}
                </div>
                <div className={`p-3 rounded-2xl ${msg.role === 'user' ? 'bg-blue-500 text-white rounded-tr-none' : 'bg-white dark:bg-gray-800 border rounded-tl-none shadow-sm'}`}>
                  <Text className={msg.role === 'user' ? 'text-white' : ''} style={{ whiteSpace: 'pre-wrap' }}>{msg.content}</Text>
                </div>
              </Space>
            </List.Item>
          )}
        />
        <div ref={messagesEndRef} />
      </div>
      <div className="flex gap-2 mt-auto">
        <Input.TextArea
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onPressEnter={(e) => {
            if (!e.shiftKey) {
              e.preventDefault();
              handleSend();
            }
          }}
          placeholder="Ask me anything... (Press Enter to send, Shift+Enter for new line)"
          autoSize={{ minRows: 2, maxRows: 6 }}
          disabled={loading}
        />
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSend}
          loading={loading}
          className="h-auto px-6"
        >
          Send
        </Button>
      </div>
    </Card>
  );
};

export default Chat;
