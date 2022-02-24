import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';
export default class Groupbox extends SvelteTypedComponent<GroupboxProps, GroupboxEvents, GroupboxSlots> {
}

declare const _GroupboxProps: {

    /** 
     * Disabled state.
     */
    disabled?: boolean;

    /** 
     * Selected values.
     */
    group?: string[];

    /** 
     * Array of Items.
     */
    allItem?: ({value:string, text: string}|string)[];

    /**
     * Title for tooltip
     */
    title?: string

} & SvelteAllProps;

declare const _GroupboxEvents: {
    change: Event;
};

declare const _GroupboxSlots: {
};

export declare type GroupboxProps = typeof _GroupboxProps;
export declare type GroupboxEvents = typeof _GroupboxEvents;
export declare type GroupboxSlots = typeof _GroupboxSlots;
export {
};
