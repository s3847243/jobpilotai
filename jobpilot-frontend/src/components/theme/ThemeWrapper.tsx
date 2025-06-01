import { useSelector } from "react-redux";
import { RootState } from "../../store";
import React, { useEffect } from 'react';


const ThemeWrapper = ({ children }: { children: React.ReactNode }) => {
  const theme = useSelector((state: RootState) => state.theme.mode);

    useEffect(() => {
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  return <>{children}</>;
};

export default ThemeWrapper;