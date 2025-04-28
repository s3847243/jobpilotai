import { Plus } from 'lucide-react'
import React from 'react'
import InterviewItem from './InterviewItem'

const Interview = () => {
  return (
        <section>
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-mono px-10 py-2">My Interviews</h1>
                <button className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10">
                    <Plus size={20} />
                    Interviews
                </button>
            </div>
            <hr className='my-3 border-t-4 py-3'/>
            <div className="grid grid-cols-4 gap-6 px-5">
                <InterviewItem name={"somethign"} date={"somethings"}/>

            </div>             
        </section>
  )
}

export default Interview
